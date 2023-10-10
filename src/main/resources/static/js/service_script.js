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
    var updateProfileButton = document.getElementById('updateProfile');

    if (updateProfileButton) {
        updateProfileButton.addEventListener('click', function() {
            document.getElementById('imageUpdate').click();
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var imageUpdateElement = document.getElementById('imageUpdate');

    // Check if the element exists
    if (imageUpdateElement) {
        imageUpdateElement.addEventListener('change', function(e) {
            if (e.target.files && e.target.files[0]) {
                const reader = new FileReader();

                reader.onload = function(e) {
                    document.getElementById('updateProfile').src = e.target.result;
                }

                reader.readAsDataURL(e.target.files[0]);
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var updatePanelElement = document.getElementById('updatePanel');

    if (updatePanelElement) {
        updatePanelElement.addEventListener('click', function() {
            closeAllPanels();
            var newScreen = document.getElementById('updateScreen');
            if (newScreen) {
                newScreen.classList.remove('hide');
                newScreen.classList.add('active');
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var updateCloseElement = document.getElementById('updateClose');

    // Check if the element exists
    if (updateCloseElement) {
        updateCloseElement.addEventListener('click', function() {
            var newScreen = document.getElementById('updateScreen');
            if (newScreen) {
                newScreen.classList.remove('active');
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
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var updatePanelElement = document.getElementById('startChat');

    if (updatePanelElement) {
        updatePanelElement.addEventListener('click', function() {
            closeAllPanels();
            var newScreen = document.getElementById('chatScreen');
            if (newScreen) {
                newScreen.classList.remove('hide');
                newScreen.classList.add('active');
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    var chatCloseElement = document.getElementById('chatClose');

    if (chatCloseElement) {
        chatCloseElement.addEventListener('click', function() {
            var newScreen = document.getElementById('chatScreen');
            if (newScreen) {
                newScreen.classList.remove('active');
            }
        });
    }
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

    if (textarea) {
        textarea.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                addMessageToChat();
            }
        });
    }

    if (sendMessageButton) {
        sendMessageButton.addEventListener('click', function() {
            addMessageToChat();
        });
    }

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

                var container = document.getElementById('myServicesTable');

                if (container) {
                    fetch('/my_service')
                    .then(response => response.text())
                    .then(html => {
                    var parser = new DOMParser();
                    var doc = parser.parseFromString(html, 'text/html');

                    var content = doc.querySelector('#myServicesTable').outerHTML;
                    var container = document.getElementById('myServicesTable').parentNode;
                    container.innerHTML = content;

                    var chatScreenHtml =
                        '<div id="chatScreen" class="hide panel">'+
                            '<h4><img src="img/chatbot_icon.png" alt="chatbot_icon" class="chatbot_icon">ChatEZ Assistant'+
                                '<button id="chatClose"><img src="img/option_icon.png" alt="option_icon" class="option_icon"></button></h4>'+
                            '<div class="chat">'+
                                '<ul id="chatContent">'+
                                    '<!-- 대화내용 -->'+
                                '</ul>'+
                            '</div>'+
                            '<p><textarea id="question" placeholder="이곳에 메세지를 입력하세요." maxlength="100" rows="1"></textarea>'+
                                '<button id="sendMessage"><img src="img/up_arrow_icon.png" alt="up_arrow_icon" class="up_arrow_icon"></button></p>'+
                        '</div>';

                    // chatScreen 요소를 삽입하는 로직
                    var trs = document.querySelectorAll('#myServicesTable tbody tr');
                    trs.forEach(tr => {
                        tr.insertAdjacentHTML('afterend', chatScreenHtml);
                    });

                    bindEventListeners();
                })
                    .catch(error => console.error('Error:', error));
                } else {
                    console.error("테이블 id 오류");
                }
            })
            .catch(error => console.error('Error:', error));
        } else {
            console.error("메타 태그 오류");
        }
    });
});

function bindEventListeners() {
    var startChatButtons = document.querySelectorAll("#startChat");
    startChatButtons.forEach(button => {
        button.addEventListener("click", function() {
            console.log("시작버튼 클릭");
            closeAllPanels();
            var newScreen = document.getElementById('chatScreen');
            if (newScreen) {
                newScreen.classList.remove('hide');
                newScreen.classList.add('active');
            }
        });
    });

    var updatePanelButtons = document.querySelectorAll("#chatClose");
    updatePanelButtons.forEach(button => {
        button.addEventListener("click", function() {
            console.log("대화창 닫기");
            var newScreen = document.getElementById('chatScreen');
            if (newScreen) {
                newScreen.classList.remove('active');
            }
        });
    });

    var textarea = document.getElementById('question');
    var chatContent = document.getElementById('chatContent');
    var sendMessageButton = document.getElementById('sendMessage');
    var chat = document.querySelector('.chat');

    if (textarea) {
        textarea.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                addMessageToChat();
            }
        });
    }

    if (sendMessageButton) {
        sendMessageButton.addEventListener('click', function() {
            addMessageToChat();
        });
    }

    function addMessageToChat() {
        var message = textarea.value.trim();
        if (message) {
            var li = document.createElement('li');
            li.textContent = message;
            chatContent.appendChild(li);
            textarea.value = '';
            textarea.style.height = 'auto';
            chat.style.height = '480px';
            if (chat.scrollTop + chat.clientHeight === chat.scrollHeight) {
                chat.scrollTop = chat.scrollHeight - chat.clientHeight;
            }
        }
    }

    document.addEventListener('input', function(e) {
        if (e.target.tagName.toLowerCase() === 'textarea' && e.target.id === 'question') {
            var initialChatHeight = chat.offsetHeight;
            var initialTextareaHeight = e.target.offsetHeight;

            e.target.style.height = 'auto';
            e.target.style.height = (e.target.scrollHeight) + 'px';

            var deltaHeight = initialTextareaHeight - e.target.offsetHeight;
            chat.style.height = (initialChatHeight + deltaHeight) + 'px';
        }
    });
}

document.addEventListener("DOMContentLoaded", function() {
    var createAiButton = document.getElementById("updateAi");
    if(createAiButton){
        createAiButton.addEventListener("click", function() {
            var updateNameValue = document.getElementById("updateName").value;
            var imageUpdateValue = document.getElementById("imageUpdate");
            var csrfMetaTag = document.querySelector('meta[name="_csrf"]');

            if (csrfMetaTag) {
                var csrfToken = csrfMetaTag.content;

                var formData = new FormData();
                formData.append("updateName", updateNameValue);
                formData.append("imageUpdate", imageUpdateValue.files[0]);

                fetch("/upload", {
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

                     var newScreen = document.getElementById("updateScreen");
                     newScreen.classList.remove('active');

                     var aiName = document.getElementById("updateName");
                     aiName.value = '';

                     var imageUpdate = document.getElementById("imageUpdate");
                     imageUpdate.value = '';

                     var updateProfile = document.getElementById("updateProfile");
                     updateProfile.src = 'img/profile_icon.png';
                })
                .catch(error => console.error('Error:', error));
            } else {
                console.error("CSRF 메타 태그 오류");
            }
        });
    }
});