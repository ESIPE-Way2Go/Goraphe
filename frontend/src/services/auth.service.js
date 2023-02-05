const API_URL = '/api/auth/';

class AuthService {
    static login(user) {
        return fetch(`${API_URL}signin`, {
            method: "POST",
            headers : {
                'Content-Type': 'application/json'
            },
            body:JSON.stringify({
                username: user.username,
                password: user.password
            })})
            .then(response => response.json()).then(response => {
                if (response.accessToken) {
                    localStorage.setItem('user', JSON.stringify(response));
                }

                return response;
            });
    }

    static logout() {
        localStorage.removeItem('user');
    }

}

//export default new AuthService();
export default AuthService;