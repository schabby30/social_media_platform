function change_username() {
    document.getElementById("username_label").style.display = "none";
    document.getElementById("username_input").style.display = "inline-block";
    document.getElementById("change_username").setAttribute('onClick', 'javascript: ok("username");');
}

function change_password() {
    document.getElementById("password_label").style.display = "none";
    document.getElementById("password_input").style.display = "inline-block";
    document.getElementById("change_password").setAttribute( 'onClick', 'javascript: ok("password");');
}

function change_name() {
    document.getElementById("name_label").style.display = "none";
    document.getElementById("name_input").style.display = "inline-block";
    document.getElementById("change_name").setAttribute( 'onClick', 'javascript: ok("name");');
}

function change_age() {
    document.getElementById("age_label").style.display = "none";
    document.getElementById("age_input").style.display = "inline-block";
    document.getElementById("change_age").setAttribute( 'onClick', 'javascript: ok("age");');
}

function ok(dataToChange) {
    document.getElementById(dataToChange + "_label").style.display = "inline-block";
    document.getElementById(dataToChange + "_input").style.display = "none";
    document.getElementById("change_" + dataToChange).setAttribute( "onClick", "javascript: change_" + dataToChange + "();" );

    var user = new FormData();
    var data = document.getElementById(dataToChange + "_input").value;
    var id = document.getElementById("userId").innerHTML;

    user.append(dataToChange, data);
    user.append("id", id);

    fetch("/save", {
        method: "POST",
        body: user
    }).then(() => {
        location.reload();
    });
}
