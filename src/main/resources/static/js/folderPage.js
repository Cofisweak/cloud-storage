//This file does not require any attention, there are a lot of bad decisions here

const fileDropArea = document.getElementById("file-drop-area");
const fileDropAdvice = document.getElementById('file-drop-advice');

["dragenter", "dragover", "dragleave", "drop"].forEach((eventName) => {
    fileDropArea.addEventListener(
        eventName,
        (e) => {
            e.preventDefault();
            e.stopPropagation();
        },
        false
    );
});

fileDropArea.addEventListener("dragenter", () => {
    fileDropArea.classList.add('file-drop-area--animated');
    fileDropAdvice.textContent = 'Drop to upload';
}, false);

fileDropArea.addEventListener("dragleave", () => {
    fileDropArea.classList.remove('file-drop-area--animated');
    fileDropAdvice.textContent = 'Drop files here to upload';
}, false);

const dragndropUploadForm = document.getElementById('dragndrop-upload-form');

const createDragndropErrorMessage = (text) => {
    const message = document.createElement("div");
    message.id = 'dragndrop-error';
    message.innerText = text
    message.style.maxWidth = '200px';
    message.style.wordBreak = 'break-word';
    message.classList.add("error-color");
    message.classList.add("mb-1");
    message.classList.add("text-center");
    fileDropAdvice.after(message);
}

fileDropArea.addEventListener("drop", (e) => {
    const dt = e.dataTransfer;
    const files = dt.files;
    if(files.length === 0) return;
    fileDropArea.classList.remove('file-drop-area--animated');
    fileDropAdvice.textContent = 'Drop files here to upload';
    const formData = new FormData(dragndropUploadForm);
    for (let file of files) {
        formData.append("files", file);
    }
    const xhr = new XMLHttpRequest();
    xhr.open('POST', dragndropUploadForm.getAttribute("action"), true);
    xhr.send(formData);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
            const parser = new DOMParser();
            const doc = parser.parseFromString(xhr.responseText, "text/html");
            const alertElement = doc.getElementById('alert');
            if (alertElement !== null) {
                const messageElement = alertElement.querySelector(".toast-body");
                const existMessage = document.getElementById('dragndrop-error');
                if (existMessage !== null) {
                    existMessage.remove();
                }
                createDragndropErrorMessage(messageElement.innerText);
            } else {
                if(xhr.status === 0) {
                    createDragndropErrorMessage('Unable to upload. Are you uploading folder?')
                    return;
                }
                location.reload()
            }
        }
    };
}, false);

const objects = document.querySelectorAll('.explorer-item');
const objectDropdowns = [];

const hideDropdowns = () => {
    objectDropdowns.forEach(e => {
        e.hide()
    });
}

const onDblClick = (url) => {
    window.getSelection().removeAllRanges();
    window.location.href = url;
};

objects.forEach(object => {
    const dropdown = new bootstrap.Dropdown(object.querySelector('.item-dots'));
    objectDropdowns.push(dropdown);
    object.addEventListener('contextmenu', (e) => {
        e.preventDefault();
        hideDropdowns();
        dropdown.toggle();
    })

});

;[].slice
    .call(document.querySelectorAll('.toast'))
    .map((toastEl) => new bootstrap.Toast(toastEl))
    .forEach(toasts => toasts.show());

const folderInput = document.getElementById('folder-input');
const folderUploadForm = document.getElementById('folder-upload-form');
folderInput.addEventListener('change', () => {
    if (folderInput.files.length === 0) return;
    folderUploadForm.submit();
});

const fileInput = document.getElementById('file-input');
const fileUploadForm = document.getElementById('file-upload-form');
fileInput.addEventListener('change', () => {
    if (fileInput.files.length === 0) return;
    fileUploadForm.submit();
})

function openRenameModal (baseUrl, oldName) {
    const renameForm = document.getElementById('rename-form');
    const renameOldName = document.getElementById('rename-old-name');
    const renameInput = document.getElementById('rename-input');
    renameOldName.setAttribute('value', oldName);
    renameInput.setAttribute('value', oldName);
    const currentPath = new URLSearchParams(window.location.search).get('path');
    const actionUrl = baseUrl + '/rename?path=' + currentPath;
    renameForm.setAttribute('action', actionUrl);
}