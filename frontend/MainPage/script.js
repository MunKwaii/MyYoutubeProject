document.addEventListener("DOMContentLoaded", () => {
  const menuBtnHeader = document.querySelector(
    ".header_left .material-symbols-outlined"
  );
  const menuBtnSidebar = document.querySelector(
    ".sidebar_expanded .button_circle"
  );
  const sidebar = document.querySelector(".sidebar_expanded");
  const overlay = document.querySelector(".overlay");
  const showMoreBtn = document.getElementById("show_more_btn");
  const subList = document.getElementById("subscription_list");

  let expanded = false;

  // Mở hoặc đóng sidebar
  function toggleSidebar(forceClose = false) {
    const shouldClose = forceClose || sidebar.classList.contains("active");
    if (shouldClose) {
      sidebar.classList.remove("active");
      overlay.classList.remove("active");
    } else {
      sidebar.classList.add("active");
      overlay.classList.add("active");
    }
  }

  // Nhấn menu header → mở
  menuBtnHeader.addEventListener("click", (e) => {
    e.stopPropagation();
    toggleSidebar();
  });

  // Nhấn menu trong sidebar → đóng
  menuBtnSidebar.addEventListener("click", (e) => {
    e.stopPropagation();
    toggleSidebar(true); // ép đóng
  });

  // Nhấn overlay → đóng
  overlay.addEventListener("click", () => {
    toggleSidebar(true);
  });

  // Nhấn Escape → đóng
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape" && sidebar.classList.contains("active")) {
      toggleSidebar(true);
    }
  });

  // Toggle Show More / Less
  showMoreBtn.addEventListener("click", () => {
    expanded = !expanded;
    subList.style.maxHeight = expanded ? "1000px" : "220px";
    showMoreBtn.querySelector("span:last-child").textContent = expanded
      ? "Show less"
      : "Show more";
    showMoreBtn.querySelector(".material-symbols-outlined").textContent =
      expanded ? "keyboard_arrow_up" : "keyboard_arrow_down";
  });

  // Danh sách kênh (mock)
  const mockChannels = [
    { name: "CodingNepal", avatar: "https://i.pravatar.cc/24?u=1" },
    { name: "TraversyMedia", avatar: "https://i.pravatar.cc/24?u=2" },
    { name: "Fireship", avatar: "https://i.pravatar.cc/24?u=3" },
    { name: "JS Mastery", avatar: "https://i.pravatar.cc/24?u=4" },
    { name: "Web Dev Simplified", avatar: "https://i.pravatar.cc/24?u=5" },
    { name: "FrontendPro", avatar: "https://i.pravatar.cc/24?u=6" },
  ];

  mockChannels.forEach((channel) => {
    const div = document.createElement("div");
    div.className = "side_ex_components";
    div.innerHTML = `
      <img class="Avatar" src="${channel.avatar}" alt="${channel.name}" />
      <span>${channel.name}</span>
    `;
    subList.appendChild(div);
  });
});

// Load video
let currentSeed = "";
let currentPage = 0;
const pageSize = 99;
let totalVideos = 0;
let currentKeyword = "";
const BASE_URL = "http://localhost:8080/identity";

// DOM toàn cục
const videoList = document.querySelector(".video_list");
const noResults = document.querySelector(".no_results");
const searchInput = document.querySelector("#search_input");
const searchForm = document.querySelector(".search_form");
const reloadPageBtn = document.getElementById("reloadSeedBtn");

async function fetchSeed() {
  const response = await fetch(`${BASE_URL}/api/videos/random-seed`);
  const data = await response.json();
  currentSeed = data.seed;
  totalVideos = data.totalVideos;
  currentPage = 0;
  console.log("New Seed:", currentSeed);
}

async function fetchVideos() {
  let url;
  if (currentKeyword) {
    url = `${BASE_URL}/api/videos/search?keyword=${encodeURIComponent(
      currentKeyword
    )}&seed=${currentSeed}&page=${currentPage}&pageSize=${pageSize}`;
  } else {
    url = `${BASE_URL}/api/videos/by-seed?seed=${currentSeed}&page=${currentPage}&pageSize=${pageSize}`;
  }
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error("Fetch failed");

    const videos = await response.json();

    if (currentKeyword && currentPage === 0 && videos.length === 0) {
      noResults.style.display = "block";
    } else {
      noResults.style.display = "none";
      renderVideos(videos);
      currentPage++;
    }
    if (
      (!currentKeyword && currentPage * pageSize >= totalVideos) || // by-seed hết
      (currentKeyword && videos.length === 0) // search hết
    ) {
      reloadPageBtn.style.display = "inline-block";
    }
  } catch (error) {
    console.error("Error fetching video:", error);
    noResults.style.display = "block";
  }
}

function renderVideos(videos) {
  videos.forEach((video) => {
    const videoCard = document.createElement("a");
    videoCard.className = "video_card";
    videoCard.href = `../PlayVideoPage/play-video.html?id=${video.id}`;

    videoCard.innerHTML = `
      <div class="thumbnail_container">
        <img src="${video.thumbnailUrl}" class="thumbnail" loading="lazy" />
        <span class="duration">${video.durationFormatted}</span>
      </div>
      <div class="video_info">
        <img src="${video.channelImageUrl}" alt="${video.channelName}" class="icon" />
        <div class="video_details">
          <h2 class="title">${video.title}</h2>
          <p class="channel_name">${video.channelName}</p>
          <p class="views">${video.viewCount} views • ${video.publishedAtFormatted}</p>
        </div>
      </div>
    `;
    videoList.appendChild(videoCard);
  });
}

async function initPage() {
  noResults.style.display = "none";
  videoList.innerHTML = ""; // Clear video cũ
  currentKeyword = "";
  reloadPageBtn.style.display = "none";
  await fetchSeed();
  await fetchVideos();
  window.scrollTo({ top: 0, behavior: "smooth" });
}

window.addEventListener("scroll", async () => {
  if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
    if (!currentKeyword && currentPage * pageSize < totalVideos) {
      await fetchVideos();
    } else if (currentKeyword) {
      await fetchVideos();
    }
  }
});

// Bắt sự kiện Search form
searchForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const keyword = searchInput.value.trim();
  if (!keyword) return;

  currentKeyword = keyword;
  currentSeed = ""; // reset random
  currentPage = 0;
  videoList.innerHTML = "";
  noResults.style.display = "none";
  await fetchVideos();
  window.scrollTo({ top: 0, behavior: "smooth" });
});

// Reset khi clear input (search với input rỗng)
searchInput.addEventListener("input", async (e) => {
  if (e.target.value.trim() === "" && currentKeyword !== "") {
    await initPage();
  }
});

// Reload seed khi F5 hoặc nút riêng
document
  .getElementById("reloadSeedBtn")
  ?.addEventListener("click", async () => {
    await initPage();
  });

// Load lần đầu
initPage();
