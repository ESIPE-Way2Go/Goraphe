import LoginPage from '@/views/LoginPage.vue'
import { mount } from "@vue/test-utils";

describe("LoginPage.vue", () => {
    let wrapper;

    beforeEach(() => {
        wrapper = mount(LoginPage);
    });

    afterEach(() => {
        wrapper.destroy;
    });

    it("renders the components", () => {
        expect(wrapper.text()).toContain('GoRaphe');
        expect(wrapper.find('input[placeholder="username"]').exists()).toBe(true);
        expect(wrapper.find('input[placeholder="Password"]').exists()).toBe(true);
        expect(wrapper.find("form").exists()).toBe(true);
    });

    it("updates the form data when input values change", () => {
        const usernameInput = wrapper.find("input#inputEmail");
        usernameInput.setValue("admin");
        expect(wrapper.vm.form.username).toBe("admin");

        const passwordInput = wrapper.find("input#inputPassword");
        passwordInput.setValue("0000");
        expect(wrapper.vm.form.password).toBe("0000");
    });

    it('submits the form', async () => {
        wrapper.find('input[placeholder="username"]').setValue('admin1');
        wrapper.find('input[placeholder="Password"]').setValue('1111');

        await wrapper.find('button').trigger('click');
    });
});
