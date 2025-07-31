// Lấy phần tử có class 'menu-icon' (ảnh icon menu 3 gạch) trong trang HTML
var menuIcon = document.querySelector(".menu-icon");

// Lấy phần tử có class 'sidebar' (menu bên trái)
var sidebar = document.querySelector(".sidebar");

// Lấy phần tử có class 'container' (vùng chứa nội dung chính)
var container = document.querySelector(".container");

// Gán sự kiện onclick (khi click vào menu icon)
menuIcon.onclick = function() {
  
    // Toggle (bật/tắt) class 'small-sidebar' trên sidebar mỗi lần bấm
    // → Nếu sidebar đang có class 'small-sidebar' thì xóa, nếu chưa có thì thêm vào
    sidebar.classList.toggle("small-sidebar");

    // Toggle class 'large-container' trên container để thay đổi bố cục nội dung
    // → Khi sidebar nhỏ lại, container sẽ được đẩy sát hơn (ít padding hơn)
    container.classList.toggle("large-container");
}


const videoId = new URLSearchParams(window.location.search).get('id');

if (videoId) {
  console.log("Video ID:", videoId);  // Kiểm tra ID có đúng không

  // Fetch video từ API
  fetch(`http://localhost:8080/identity/api/videos/${videoId}`)
    .then(response => {
      if (!response.ok) {
        throw new Error('Video không tồn tại');
      }
      return response.json();
    })
    .then(video => {
      const youtubeUrl = video.videoUrl;
      const videoCode = new URL(youtubeUrl).searchParams.get("v");
      const embedUrl = `https://www.youtube.com/embed/${videoCode}?autoplay=1`;

      // Gán video vào iframe
      document.getElementById("youtubePlayer").src = embedUrl;
      document.getElementById("videoTitle").textContent = video.title;
    })
    .catch(err => console.error('Lỗi khi fetch video:', err));
} else {
  console.error('Không có tham số "id" trong URL');
}