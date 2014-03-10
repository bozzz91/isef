function remember(login, pass) {
    window.localStorage.setItem('login', login);
    window.localStorage.setItem('pass', pass);
}

function restore() {
    document.getElementsByClassName('login-input')[0].value = window.localStorage.getItem('login');
    document.getElementsByClassName('pass-input')[0].value = window.localStorage.getItem('pass');

    document.getElementsByClassName('pass-input')[0].focus();
    document.getElementsByClassName('login-input')[0].focus();
}

var client;
function enableClipboard() {
    client = new ZeroClipboard($("button.copy"), {
        moviePath: "/js/ZeroClipboard.swf"
    });

    client.on("load", function(client) {
        // alert( "movie is loaded" );

        client.on("complete", function(client, args) {
            // `this` is the element that was clicked
            alert("Ссылка скопирована в буфер");
        });
    });
}