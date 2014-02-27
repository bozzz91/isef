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