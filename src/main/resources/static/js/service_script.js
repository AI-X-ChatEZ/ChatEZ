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

document.getElementById("uploadProfile").addEventListener("click", function() {
    document.getElementById("imageInput").click();
});

document.getElementById("imageInput").addEventListener("change", function() {
    console.log(this.files);
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

document.addEventListener('DOMContentLoaded', function() {
    // 'profile_icon' 클래스를 가진 모든 요소 선택
    var profileIcons = document.querySelectorAll('.profile_icon');

    profileIcons.forEach(function(icon) {
        icon.addEventListener('click', function() {
            var panel = icon.closest('.panel');
            var serviceName = panel.querySelector('.serviceName').textContent;

            panel.querySelector('.imageUpdate').click();
        });
    });

    // 'imageUpdate' 클래스를 가진 모든 파일 입력 상자 선택
    var imageUpdateElements = document.querySelectorAll('.imageUpdate');

    // 각 파일 입력 상자에 대한 이벤트 리스너 추가
    imageUpdateElements.forEach(function(input) {
        input.addEventListener('change', function(e) {
            // Find the closest '.panel' element
            var panel = input.closest('.panel');

            var serviceName = panel.querySelector('.serviceName').textContent;
            console.log(serviceName);

            if (e.target.files && e.target.files[0]) {
                const reader = new FileReader();

                reader.onload = function(e) {
                    panel.querySelector('.profile_icon').src = e.target.result;
                };

                reader.readAsDataURL(e.target.files[0]);
            }
        });
    });
});

document.addEventListener('DOMContentLoaded', function() {
    // Update Panel
    var updatePanelElements = document.querySelectorAll('.updatePanel');

     updatePanelElements.forEach(function(updatePanelElement) {
        updatePanelElement.addEventListener('click', function() {
            closeAllPanels();
            var serviceName = updatePanelElement.getAttribute('data-service-name');
            var updateScreen = document.getElementById('updateScreen-' + serviceName);
            if (updateScreen) {
                updateScreen.classList.remove('hide');
                updateScreen.classList.add('active');
            }
        });
    });

    // Update Close
    var updateCloseElements = document.querySelectorAll('.updateClose');

    updateCloseElements.forEach(function(updateCloseElement) {
        updateCloseElement.addEventListener('click', function() {
            var updateScreen = updateCloseElement.closest('.updateScreen');
            if (updateScreen) {
                updateScreen.classList.remove('active');
            }

            var aiName = document.getElementById('updateName');
            if (aiName) {
                aiName.value = '';
            }

            var imageUpdate = document.getElementById('imageUpdate');
            if (imageUpdate) {
                imageUpdate.value = '';
            }

            var updateProfile = document.getElementById('updateProfile');
            if (updateProfile) {
                updateProfile.src = 'img/profile_icon.png';
            }
        });
    });

   // Start Chat
    var startChatElements = document.querySelectorAll('.startChat');
    startChatElements.forEach(function(startChatElement) {
        startChatElement.addEventListener('click', function() {
            closeAllPanels();
            var serviceName = startChatElement.getAttribute('data-service-name');
            var chatScreen = document.getElementById('chatScreen-' + serviceName);
            if (chatScreen) {
                chatScreen.classList.remove('hide');
                chatScreen.classList.add('active');
            }
        });
    });

    // Chat Close
    var chatCloseElements = document.querySelectorAll('.chatClose');
    chatCloseElements.forEach(function(chatCloseElement) {
        chatCloseElement.addEventListener('click', function() {
            // `chatClose` 버튼의 부모 `.panel` 요소를 찾습니다.
            var chatPanel = chatCloseElement.closest('.panel');

            if (chatPanel) {
                chatPanel.classList.add('hide');
                chatPanel.classList.remove('active');
            }
        });
    });
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
    var chatAreas = document.querySelectorAll('.chatEZ_list .chatScreen');

    chatAreas.forEach(function(chatArea) {
        var textarea = chatArea.querySelector('textarea');
        var chatContent = chatArea.querySelector('#chatContent');
        var sendMessageButton = chatArea.querySelector('#sendMessage');
        var chat = chatArea.querySelector('.chat');

        if (textarea) {
            textarea.addEventListener('keydown', function (e) {
                if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    addMessageToChat(textarea, chatContent, chat);
                }
            });
        }

        if (sendMessageButton) {
            sendMessageButton.addEventListener('click', function() {
                addMessageToChat(textarea, chatContent, chat);
            });
        }
    });

    function addMessageToChat(textarea, chatContent, chat) {
        var message = textarea.value.trim();
        if (message) {
            var li = document.createElement('li');
            li.textContent = message;
            chatContent.appendChild(li);
            textarea.value = '';
            textarea.style.height = 'auto';
            chat.scrollTop = chat.scrollHeight;
        }
    }
    var resetButtons = document.querySelectorAll('.chatReset');
        resetButtons.forEach(function(resetButton) {
            resetButton.addEventListener('click', function() {
                var chatContent = this.closest('.chatScreen').querySelector('#chatContent');
                chatContent.innerHTML = ''; // 대화 내용을 비움
            });
        });
});

document.addEventListener('input', function(e) {
    if(e.target.tagName.toLowerCase() === 'textarea' && e.target.closest('.chatScreen')) {
        var chat = e.target.closest('.chatScreen').querySelector('.chat');
        var initialChatHeight = chat.offsetHeight;
        var initialTextareaHeight = e.target.offsetHeight;

        e.target.style.height = 'auto';
        e.target.style.height = (e.target.scrollHeight) + 'px';

        var deltaHeight = initialTextareaHeight - e.target.offsetHeight;
        chat.style.height = (initialChatHeight + deltaHeight) + 'px';
    }
});

document.addEventListener("DOMContentLoaded", function() {
    var createAiButton = document.getElementById("createAi");
    createAiButton.addEventListener("click", function() {
        var aiNameValue = document.getElementById("aiName").value;
        var fileInput = document.getElementById("imageInput");
        var csrfMetaTag = document.querySelector('meta[name="_csrf"]');

        if (csrfMetaTag) {
            var csrfToken = csrfMetaTag.content;

            var formData = new FormData();
            formData.append("aiName", aiNameValue);
            formData.append("imageFile", fileInput.files[0]);

            fetch("/upload", {
                method: "POST",
                headers: {
                    "X-CSRF-TOKEN": csrfToken,
                },
                body: formData,
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error("에러가 발생하였습니다.");
                }
                return response.text();
            })
            .then(data => {
                console.log(data);
                window.location.reload();
                setTimeout(function() {
                var newScreen = document.getElementById("newScreen");
                if(newScreen){
                    newScreen.classList.remove('active');
                }

                var aiName = document.getElementById("aiName");
                if(aiName){
                    aiName.value = '';
                }

                var imageInput = document.getElementById("imageInput");
                if(imageInput){
                    imageInput.value = '';
                }

                var uploadProfile = document.getElementById("uploadProfile");
                if(uploadProfile){
                    uploadProfile.src = 'img/profile_icon.png';
                }

                var fileInput = document.getElementById("fileInput");
                if(fileInput){
                    fileInput.value = '';
                }

                var fileList = document.getElementById("fileList");
                if(fileList){
                    while (fileList.firstChild) {
                        fileList.removeChild(fileList.firstChild);
                    }
                }
                }, 500);  // 5000 밀리초 = 5초
            })
            .catch(error => console.error('Error:', error));
        } else {
            console.error("CSRF 메타 태그가 존재하지 않습니다.");
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    document.addEventListener('click', function (event) {
        if (event.target.classList.contains('updateAi')) {
            var serviceName = event.target.id.replace('updateAi-', '');
            var serviceNo = document.getElementById('serviceNo-' + serviceName).textContent;

            var updateAiButton = document.getElementById("updateAi-" + serviceName);

            if (updateAiButton) {
                console.log("updateAiButton button clicked");
                var aiNameInput = document.getElementById('updateName-' + serviceName).value;
                var imageInput = document.getElementById('imageUpdate-' + serviceName).files[0];
                var csrfMetaTag = document.querySelector('meta[name="_csrf"]');
                console.log(serviceNo);
                if (csrfMetaTag) {
                    var csrfToken = csrfMetaTag.content;

                    var formData = new FormData();
                    formData.append("selectNo", serviceNo);
                    formData.append("updateName", aiNameInput);
                    formData.append("updateFile", imageInput);

                    fetch("/update", {
                        method: "POST",
                        headers: {
                            "X-CSRF-TOKEN": csrfToken,
                        },
                        body: formData,
                    })
                        .then(response => {
                            if (!response.ok) {
                                throw new Error("네트워크 오류");
                            }
                            return response.text();
                        })
                        .then(data => {
                            console.log(data);

                            var newScreen = document.getElementById("updateScreen-" + serviceName);
                            newScreen.classList.remove('active');

                            var aiName = document.getElementById("updateName-" + serviceName);
                            aiName.value = '';

                            var imageUpdate = document.getElementById("imageUpdate-" + serviceName);
                            imageUpdate.value = '';

                            var updateProfile = document.getElementById("updateProfile-" + serviceName);
                            updateProfile.src = 'img/profile_icon.png';
                        })
                        .catch(error => console.error('Error:', error));
                } else {
                    console.error("CSRF 메타 태그 오류");
                }
            }
        }
    });
});