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
const pageSize = 100;
let totalVideos = 0;
const BASE_URL = "http://localhost:8080/identity";

async function fetchSeed() {
  const response = await fetch(`${BASE_URL}/api/videos/random-seed`);
  const data = await response.json();
  currentSeed = data.seed;
  totalVideos = data.totalVideos;
  currentPage = 0;
  console.log("New Seed:", currentSeed);
}

async function fetchVideos() {
  const response = await fetch(
    `${BASE_URL}/api/videos/by-seed?seed=${currentSeed}&page=${currentPage}&pageSize=${pageSize}`
  );
  const videos = await response.json();
  renderVideos(videos);
  currentPage++;
}

function renderVideos(videos) {
  const videoList = document.querySelector(".video_list");
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
  const videoList = document.querySelector(".video_list");
  videoList.innerHTML = ""; // Clear video cũ
  await fetchSeed();
  await fetchVideos();
}

window.addEventListener("scroll", async () => {
  if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) {
    if (currentPage * pageSize < totalVideos) {
      await fetchVideos();
    } else {
      console.log("Reached end of videos for this seed");
    }
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
