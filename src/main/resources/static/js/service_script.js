function closeAllPanels() {
    var panels = document.querySelectorAll('.panel.active');
    panels.forEach(function(panel) {
        panel.classList.remove('active');
    });
}
document.getElementById("showPanel").addEventListener("click", function() {
    closeAllPanels();
    var newScreen = document.getElementById("newScreen");
    newScreen.classList.remove('hide');
    newScreen.classList.add('active');
});

document.getElementById("createClose").addEventListener("click", function() {
    var newScreen = document.getElementById("newScreen");
    newScreen.classList.remove('active');

    var aiName = document.getElementById("aiName");
    aiName.value = '';

    var imageInput = document.getElementById("imageInput");
    imageInput.value = '';

    var uploadProfile = document.getElementById("uploadProfile");
    uploadProfile.src = 'img/profile_icon.png';

    var fileInput = document.getElementById("fileInput");
    fileInput.value = '';

    var fileList = document.getElementById("fileList");
    while (fileList.firstChild) {
        fileList.removeChild(fileList.firstChild);
    }
});

document.getElementById("fileUpload").addEventListener("click", function() {
    document.getElementById("fileInput").click();
});

document.getElementById("imageInput").addEventListener("change", function() {
    console.log(this.files);
});

document.getElementById('uploadProfile').addEventListener('click', function() {
    document.getElementById('imageInput').click();
});

document.getElementById('imageInput').addEventListener('change', function(e) {
    if (e.target.files && e.target.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            document.getElementById('uploadProfile').src = e.target.result;
        }

        reader.readAsDataURL(e.target.files[0]);
    }
});

document.getElementById('updateProfile').addEventListener('click', function() {
    document.getElementById('imageUpdate').click();
});

document.getElementById('imageUpdate').addEventListener('change', function(e) {
    if (e.target.files && e.target.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            document.getElementById('updateProfile').src = e.target.result;
        }

        reader.readAsDataURL(e.target.files[0]);
    }
});

document.getElementById("updatePanel").addEventListener("click", function() {
    closeAllPanels();
    var newScreen = document.getElementById("updateScreen");
    newScreen.classList.remove('hide');
    newScreen.classList.add('active');
});

document.getElementById("updateClose").addEventListener("click", function() {
    var newScreen = document.getElementById("updateScreen");
    newScreen.classList.remove('active');

    var aiName = document.getElementById("updateName");
    aiName.value = '';

    var imageUpdate = document.getElementById("imageUpdate");
    imageUpdate.value = '';

    var updateProfile = document.getElementById("updateProfile");
    updateProfile.src = 'img/profile_icon.png';
});

document.getElementById("startChat").addEventListener("click", function() {
    closeAllPanels();
    var newScreen = document.getElementById("chatScreen");
    newScreen.classList.remove('hide');
    newScreen.classList.add('active');
});


document.getElementById("chatClose").addEventListener("click", function() {
    var newScreen = document.getElementById("chatScreen");
    newScreen.classList.remove('active');
});

document.getElementById('fileInput').addEventListener('change', function () {
    var fileList = document.getElementById('fileList');
    var files = this.files;

    for (var i = 0; i < files.length; i++) {
        var file = files[i];
        var listItem = document.createElement('li');
        listItem.innerHTML = '<img src="/img/txt-file.png" alt="txt-file" class="txt-file">' + file.name +
        '<span><img src="/img/close_icon.png" alt="close-icon" class="close-icon" onclick="removeFile(this)"></span>';
        fileList.appendChild(listItem);
    }
});

function removeFile(element) {
    var listItem = element.closest('li');
    if (listItem) {
        listItem.parentNode.removeChild(listItem);
    }
}

document.addEventListener('DOMContentLoaded', function () {
    var textarea = document.getElementById('question');
    var chatContent = document.getElementById('chatContent');
    var sendMessageButton = document.getElementById('sendMessage');
    var chat = document.querySelector('.chat');

    textarea.addEventListener('keydown', function (e) {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            addMessageToChat();
        }
    });

    sendMessageButton.addEventListener('click', function() {
        addMessageToChat();
    });

    function addMessageToChat() {
        var message = textarea.value.trim();
        if (message) {
            var li = document.createElement('li');
            li.textContent = message;
            chatContent.appendChild(li);
            textarea.value = '';
            textarea.style.height = 'auto';
            chat.style.height = '480px';
            if(chat.scrollTop + chat.clientHeight === chat.scrollHeight) {
                chat.scrollTop = chat.scrollHeight - chat.clientHeight;
            }
        }
    }
});

document.addEventListener('input', function(e) {
    if(e.target.tagName.toLowerCase() === 'textarea' && e.target.id === 'question') {
        var chat = document.querySelector('.chat');
        var initialChatHeight = chat.offsetHeight;
        var initialTextareaHeight = e.target.offsetHeight;

        e.target.style.height = 'auto';
        e.target.style.height = (e.target.scrollHeight) + 'px';

        var deltaHeight = initialTextareaHeight - e.target.offsetHeight;
        chat.style.height = (initialChatHeight + deltaHeight) + 'px';
    }
});