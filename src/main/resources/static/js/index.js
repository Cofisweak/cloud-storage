//drag n drop
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

fileDropArea.addEventListener("drop", (e) => {}, false);
