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
    var profileIcons = document.querySelectorAll('.update_profile_icon');

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
                    panel.querySelector('.update_profile_icon').src = e.target.result;
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
        textarea.disabled = true; // textarea를 비활성화
        textarea.placeholder = "답변을 기다리는 중...";
        fetch('http://localhost:8000/handle_query', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({query: message}) // 쿼리를 JSON 형식으로 변환
        }).then(response => response.json())
            .then(data => {
                console.log(data);
                addServerResponseToChat(data, chatContent, chat);
                textarea.disabled = false; // textarea를 다시 활성화
                textarea.placeholder = "이곳에 메세지를 입력하세요."; // placeholder 값을 원래대로 복구
            }).catch(error => {
                console.error('Error:', error); // 오류를 콘솔에 출력
                textarea.disabled = false; // 오류 발생 시 textarea를 다시 활성화
                textarea.placeholder = "이곳에 메세지를 입력하세요.";
            });

            var li = document.createElement('li');
            li.textContent = message;
            chatContent.appendChild(li);
            textarea.value = '';
            textarea.style.height = 'auto';
            chat.scrollTop = chat.scrollHeight;
        }
    }

    function addServerResponseToChat(data, chatContent, chat) {
        var li = document.createElement('li');
        li.textContent = data;  //서버로부터 받은 메시지
        li.classList.add('server-response'); // 답변 CSS 클래스 적용
        chatContent.appendChild(li);
        chat.scrollTop = chat.scrollHeight;
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

var selectedFiles = [];  // 현재 선택된 파일을 저장하는 배열

document.getElementById('fileInput').addEventListener('change', function () {
    var fileList = document.getElementById('fileList');
    var files = this.files;

    for (var i = 0; i < files.length; i++) {
        var file = files[i];
        selectedFiles.push(file);  // 파일을 selectedFiles 배열에 추가

        var listItem = document.createElement('li');
        listItem.setAttribute('data-fileindex', selectedFiles.length - 1);  // 파일 인덱스 저장
        listItem.innerHTML = '<img src="/img/txt-file.png" alt="txt-file" class="txt-file">' + file.name +
        '<span><img src="/img/close_icon.png" alt="close-icon" class="close-icon" onclick="removeFile(this)"></span>';
        fileList.appendChild(listItem);
    }
});

function removeFile(element) {
    var listItem = element.closest('li');
    if (listItem) {
        var fileIndex = listItem.getAttribute('data-fileindex');  // 파일 인덱스 가져오기
        selectedFiles[fileIndex] = null;  // 해당 인덱스의 파일 제거
        listItem.parentNode.removeChild(listItem);
    }
    document.getElementById('fileInput').value = '';
}

document.addEventListener("DOMContentLoaded", function() {
    var createAiButton = document.getElementById("createAi");
    createAiButton.addEventListener("click", function() {
        var aiNameValue = document.getElementById("aiName").value;
        var imageInput = document.getElementById("imageInput");
        var fileInput = document.getElementById("fileInput").files;
        var csrfMetaTag = document.querySelector('meta[name="_csrf"]');

        var validFilesCount = selectedFiles.filter(file => file !== null).length;

        if (aiNameValue === "" || imageInput.length === 0 || validFilesCount === 0) {
            alert("AI 이름, 프로필 이미지와 파일을 모두 선택해주세요.");
            return;
        }

        // FastAPI 서버로 fileInput의 파일 전송
        var fileFormData = new FormData();
        fileFormData.append('index', aiNameValue);
        var validFiles = selectedFiles.filter(file => file !== null);

        for (var i = 0; i < validFiles.length; i++) {
            fileFormData.append("files", validFiles[i]);
        }

        fetch("http://localhost:8000/upload_files", {
            method: "POST",
            body: fileFormData,
        })
        .then(response => {
            if (!response.ok) {
                throw new Error("네트워크 오류가 발생했습니다.");
            }
            return response.json();
        })
        .then(data => {
            console.log('파일이 성공적으로 업로드되었습니다:', data);
            window.location.reload();
        })
        .catch(error => console.error('에러:', error));

        if (csrfMetaTag) {
            var csrfToken = csrfMetaTag.content;

            // 기존 엔드포인트로 나머지 데이터 전송
            var formData = new FormData();
            formData.append("aiName", aiNameValue);
            formData.append("imageFile", imageInput.files[0]);

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
                var aiNameInput = document.getElementById('updateName-' + serviceName).value;
                var imageInput = document.getElementById('imageUpdate-' + serviceName).files[0];
                var csrfMetaTag = document.querySelector('meta[name="_csrf"]');

                if (aiNameInput === "") {
                    alert("수정할 AI 이름을 작성해 주세요.");
                    return;
                }

                if (csrfMetaTag) {
                    var csrfToken = csrfMetaTag.content;

                    var formData = new FormData();
                    formData.append("selectNo", serviceNo);
                    formData.append("updateName", aiNameInput);
                    if (imageInput) {
                        formData.append("updateFile", imageInput);
                    }
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
                            window.location.reload();
                            setTimeout(function() {
                                var newScreen = document.getElementById("updateScreen-" + serviceName);
                                newScreen.classList.remove('active');

                                var aiName = document.getElementById("updateName-" + serviceName);
                                aiName.value = '';

                                var imageUpdate = document.getElementById("imageUpdate-" + serviceName);
                                imageUpdate.value = '';

                                var updateProfile = document.getElementById("updateProfile-" + serviceName);
                                updateProfile.src = 'img/profile_icon.png';
                            }, 500);  // 5000 밀리초 = 5초
                        })
                        .catch(error => console.error('Error:', error));
                } else {
                    console.error("CSRF 메타 태그 오류");
                }
            }
        }
    });
});

document.addEventListener("DOMContentLoaded", function () {
    var deleteButtons = document.querySelectorAll(".deleteChat");

    deleteButtons.forEach(function (deleteAiButton) {
        deleteAiButton.addEventListener("click", function () {
            var serviceNo = deleteAiButton.getAttribute('data-service-no');
            var csrfMetaTag = document.querySelector('meta[name="_csrf"]');

            if (csrfMetaTag) {
                var csrfToken = csrfMetaTag.content;

                var formData = new FormData();
                formData.append("serviceNo", serviceNo);

                fetch("/delete", {
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
                    window.location.reload();
                })
                .catch(error => console.error('Error:', error));
            }
        });
    });
});