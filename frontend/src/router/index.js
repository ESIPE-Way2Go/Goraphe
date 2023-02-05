import { createRouter, createWebHistory } from 'vue-router'



// 2. Define some routes
// Each route should map to a component.
// We'll talk about nested routes later.
const routes = [
    {   path: '/example',
        name: 'example',
        component: () => import('@/views/HelloWorld.vue'),
        meta: {requiresAuth: false, layout: 'content'}
    },
    {   path: '/login',
        name: "login",
        component: () => import('@/views/LoginPage.vue'),
        meta: {requiresAuth: false, layout: 'blank'}
    },
    {   path: '/',
        name: 'home',
        component: () => import('@/views/HomePage.vue'),
        meta: {requiresAuth: true, layout: 'content'}
    },
    {   path: '/:pathMatch(.*)*',
        name: "error-404",
        component: () => import('@/views/Error404.vue'),
        meta: {requiresAuth: false, layout: 'blank'}
    },
]

// 3. Create the router instance and pass the `routes` option
// You can pass in additional options here, but let's
// keep it simple for now.
const router = createRouter({
    // 4. Provide the history implementation to use. We are using the hash history for simplicity here.
    history: createWebHistory(),
    routes, // short for `routes: routes`
})

router.beforeEach((to,from,next)=>{

    const loggedIn = localStorage.getItem('user');
    const isLogged = loggedIn !==null;
    if(!to.meta.requiresAuth){
        next();
        //nedd to be logged
    } else if(!isLogged){
        next({name: "login"});
    //token expire
    }else if(parseJwt(JSON.parse(loggedIn).accessToken).exp< Date.now()/1000) {
        localStorage.removeItem('user');
        next({name: "login"})
    } else if(to.name ==="login"){
            next({name: from.name})
    }else{
        next()
    }
})

export default router;

function parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
}