import {getCurrentInstance, reactive, toRefs, watch} from "vue";

export const useRouter = () => {
    const vm = getCurrentInstance().proxy
    const state = reactive({
        route: vm.$route,
    })

    watch(() => vm.$route, r => {
        state.route = r
    },)

    return {...toRefs(state), router: vm.$router}
}

export const _ = null

/*
 * This method allows to filter access to only the people authorized to access it
 * Using can("admin") give access only to the administrators
 * Using can("user") restrict access to the guests
 * Using can() with any other String will return false
 */
export const can = (role) => {
    if (role === null) {
        return false;
    }
    const user = JSON.parse(localStorage.getItem('user'));
    if (user === null) return false;
    const role_user = user.roles[0]
    if (role_user === null) {
        return false;
    }
    switch (role_user) {
        case "ROLE_ADMIN" :
            return true // The admins can access all the things
        case "ROLE_USER" :
            return role === "user"
        default :
            return false
    }
}