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
const videoId = new URLSearchParams(window.location.search).get("id");
console.log("Video ID:", videoId); // Kiểm tra ID có đúng không

if (videoId) {
  console.log("Video ID:", videoId); // Kiểm tra ID có đúng không

  // Fetch video từ API
  fetch(`http://localhost:8080/identity/api/videos/${videoId}`)
    .then((response) => {
      if (!response.ok) {
        throw new Error("Video không tồn tại");
      }
      return response.json();
    })
    .then((video) => {
      const youtubeUrl = video.videoUrl;
      const videoCode = new URL(youtubeUrl).searchParams.get("v");
      const embedUrl = `https://www.youtube.com/embed/${videoCode}?autoplay=1`;

      // Gán video vào iframe
      document.getElementById("youtubePlayer").src = embedUrl;
      document.getElementById("videoTitle").textContent = video.title;
    })
    .catch((err) => console.error("Lỗi khi fetch video:", err));
} else {
  console.error('Không có tham số "id" trong URL');
}
