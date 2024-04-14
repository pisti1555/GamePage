function openPopUp(id) {
    let window = document.getElementById(id);
    window.classList.remove("closed");
}

function closePopUp(id) {
    let window = document.getElementById(id);
    window.classList.add("closed");
}