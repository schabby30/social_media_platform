const url = "https://api.cloudinary.com/v1_1/schabby/image/upload";


function upload(userName, userId) {
    
    var uploaded_url = document.getElementById("profile_picture").src;
    var fileName = "profile_picture_";
    
    const file = document.querySelector("[type=file]").files[0];
    const formData = new FormData();
    
    var ordinal = parseInt(uploaded_url.substring(uploaded_url.length - 6, uploaded_url.length - 4)) + 1;
    
    if (ordinal < 10) 
        fileName += "0" + ordinal.toString(); 
    else fileName += ordinal.toString();
    
    var blob = file.slice(0, file.size, 'image/jpeg');
    var newFile = new File([blob], fileName + ".jpg", { type: 'image/jpg' });
    
    formData.append("file", newFile);
    formData.append("upload_preset", "edrpmtup");
    formData.append("folder", userName);

    fetch(url, {
            method: "POST",
            body: formData
        })
        .then((response) => {
            return response.text();
        })
        .then((data) => {
            document.getElementById("profile_picture").src = JSON.parse(data)["url"];
            var xhttp = new XMLHttpRequest();
            xhttp.open("GET", "/profil/" + userId + "/" + userId + "?url=" + JSON.parse(data)["url"], true);
            xhttp.send();
    });
}
