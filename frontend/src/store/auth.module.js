import AuthService from '../services/auth.service';

const user_info = JSON.parse(localStorage.getItem('user'));
const initialState = user_info
    ? { status: { loggedIn: true }, user: user_info }
    : { status: { loggedIn: false }, user: null };

export const auth = {
    namespaced: true,
    state: initialState,
    actions: {
        login({ commit }, user) {
             return AuthService.login(user).then(
                 userA => {
                     commit('loginSuccess', userA);
                     return Promise.resolve(userA);
                 },
                 error => {
                     commit('loginFailure');
                     return Promise.reject(error);
                 })
        },
        logout({ commit }) {
            AuthService.logout();
            commit('logout');
        },

    },
    mutations: {
        loginSuccess(state, user) {
            state.status.loggedIn = true;
            state.user = user;
        },
        loginFailure(state) {
            state.status.loggedIn = false;
            state.user = null;
        },
        logout(state) {
            state.status.loggedIn = false;
            state.user = null;
        },
        registerSuccess(state) {
            state.status.loggedIn = false;
        },
        registerFailure(state) {
            state.status.loggedIn = false;
        }
    }
};