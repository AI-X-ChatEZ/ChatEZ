document.getElementById("showPanel").addEventListener("click", function() {
    var newScreen = document.getElementById("newScreen");
    newScreen.classList.remove('hide');
    newScreen.classList.add('active');
});

document.getElementById("createClose").addEventListener("click", function() {
    var newScreen = document.getElementById("newScreen");
    newScreen.classList.remove('active');
});

document.getElementById("fileUpload").addEventListener("click", function() {
    // 숨겨진 input 태그를 트리거합니다.
    document.getElementById("fileInput").click();
});

document.getElementById("fileInput").addEventListener("change", function() {
    // 파일이 선택되었을 때 여기서 처리합니다.
    console.log(this.files);
});

document.getElementById('uploadProfile').addEventListener('click', function() {
    document.getElementById('fileInput').click();
});

// 파일이 선택되면 이미지를 업데이트
document.getElementById('fileInput').addEventListener('change', function(e) {
    if (e.target.files && e.target.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            document.getElementById('uploadProfile').src = e.target.result;
        }

        reader.readAsDataURL(e.target.files[0]);
    }
});

document.getElementById('updateProfile').addEventListener('click', function() {
    document.getElementById('fileInput').click();
});

// 파일이 선택되면 이미지를 업데이트
document.getElementById('fileInput').addEventListener('change', function(e) {
    if (e.target.files && e.target.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            document.getElementById('updateProfile').src = e.target.result;
        }

        reader.readAsDataURL(e.target.files[0]);
    }
});

document.getElementById("updatePanel").addEventListener("click", function() {
    var newScreen = document.getElementById("updateScreen");
    newScreen.classList.remove('hide');
    newScreen.classList.add('active');
});

document.getElementById("updateClose").addEventListener("click", function() {
    var newScreen = document.getElementById("updateScreen");
    newScreen.classList.remove('active');
});

document.getElementById("startChat").addEventListener("click", function() {
    var newScreen = document.getElementById("chatScreen");
    newScreen.classList.remove('hide');
    newScreen.classList.add('active');
});

document.getElementById("chatClose").addEventListener("click", function() {
    var newScreen = document.getElementById("chatScreen");
    newScreen.classList.remove('active');
});