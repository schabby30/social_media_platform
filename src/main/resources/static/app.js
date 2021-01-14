var urlEndPoint = "/checkIn";
var eventSource = new EventSource(urlEndPoint);
var myId;

eventSource.addEventListener("invitation", function(event){
    
    var data = JSON.parse(event.data);
    
    console.log("my ID : " + myId);
    console.log(myId === data[0]);
    console.log(myId === data[1]);
    
    if (myId === data[0])  
        window.open("/chatWith/" + data[0] + "/" + data[1] + "/" + data[2]);
    else if (myId === data[1] && !document.getElementById('chatRequest').hasChildNodes()) {
        const button = document.createElement('button');
        const div = document.createElement('div');
        
        button.setAttribute('type', 'button');
        button.addEventListener('click', function(){
            chatRequest(data);
        });
        
        button.textContent = 'Oké';
        div.textContent = data[3] + "\r\ncsetelni hívott\r\n";
        div.id = data[0];
        div.style = "white-space: pre; margin: 50px; padding: 10px; font-size: 16px; background-color: #b01a1a30; border-radius: 10px; box-shadow: 3px 3px 5px 3px  #640000A3";
        div.append(button);
        document.getElementById('chatRequest').append(div);
    }
});

window.addEventListener('unload', function() {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/leave/" + myId, true);
    xhttp.send();
});

function chatRequest(data) {
    var requestDiv = document.getElementById("chatRequest");
    
    requestDiv.removeChild(document.getElementById(data[0]));
    
    window.open('/chatWith/' + data[1] + '/' + data[0] + '/' + data[2]);
}

function chatWith(from, to) {
    
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "/invite/" + from + "/" + to, true);
    xhttp.send();
    
}

function deletePost(postId, userId) {
    var xhttp = new XMLHttpRequest();
    xhttp.open("GET", "/deletePost/" + postId, true);
    xhttp.send();
    document.getElementById("posts").removeChild(document.getElementById(userId + 'post' + postId));
}

function createChatButton(from, to, tagId) {
    
    if (from !== to) {
        
        const button = document.createElement('button');
        
        button.setAttribute('type', 'button');
        
        button.setAttribute('onclick', 'chatWith("' + from + '","' + to + '")');
        
        button.textContent = 'chat vele';
        
        button.style = "font-size: 16px; font-family: Ink Free";
        
        document.getElementById(tagId).appendChild(button);
    
    } else 
        
        myId=from;
}

function createDeleteButton(post) {
    
    if (post.user.id === myId) {
        
        const button = document.createElement('button');
        
        button.setAttribute('type', 'button');

        button.setAttribute('onclick', 'deletePost("' + post.id + '","' + post.user.id + '")');

        button.textContent = 'Törlés';

        button.style = "font-size: 16px; font-family: Ink Free";

        document.getElementById("post" + post.id).appendChild(button);
    }
    
}

