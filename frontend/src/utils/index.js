import {getCurrentInstance, reactive, toRefs, watch } from "vue";

export const useRouter = () => {
    const vm = getCurrentInstance().proxy
    const state = reactive({
        route: vm.$route,
    })

    watch(
        () => vm.$route,
        r => {
            state.route = r
        },
    )

    return {...toRefs(state), router: vm.$router}
}

export const _ = null

export const can = (role) => {
    if(role===null){
        return false;
    }
    const user = JSON.parse(localStorage.getItem('user'));
    if(user===null) return false;
    const role_user = user.roles[0]
    if(role_user === null){
        return false;
    }
    switch (role_user) {
        case "ROLE_ADMIN" :
            return role === "admin"
        default :
            return role === "user"
    }
}