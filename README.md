<div align="center">
  <a href="https://github.com/ESIPE-Way2Go/Goraphe">
    <img src="/frontend/src/assets/Goraphe_small_no_text.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Goraphe</h3>

  <p align="center">
      This project is a web application to analyze the impact of events (accidents, works, speed limit reduction) on roads. 
  </p>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
![Screenshot from 2023-03-03 15-35-27](https://user-images.githubusercontent.com/58255353/222748570-a90337ce-0c46-4b6a-8119-e3369e416718.png)

This project can be select two points and have the 

Here's why:
* Your time should be focused on creating something amazing. A project that solves a problem and helps others
* You shouldn't be doing the same tasks over and over like creating a README from scratch
* You should implement DRY principles to the rest of your life :smile:

Of course, no one template will serve all projects since your needs may be different. So I'll be adding more in the near future. You may also suggest changes by forking this repo and creating a pull request or opening an issue. Thanks to all the people have contributed to expanding this template!

Use the `BLANK_README.md` to get started.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



### Built With

This project was built with the tehchnologies next
- [Docker](https://www.docker.com/)
- [Vue](https://vuejs.org/)
- [Java](https://www.java.com/en/)
- [PostgreSQL](https://www.postgresql.org/)
- [Maven](https://maven.apache.org/)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Requirements
#### Docker
1. Install [docker](https://docs.docker.com/desktop/)

#### Gmail (SMTP)
1. You must have an email account to use the administration features like create account, invite people.
2. Create an email account 
3. Go to the [account](https://myaccount.google.com/u/1/?utm_source=OGB&utm_medium=app)
4. Go to the security tab
5. Adds dual authentication in your google account
6. Create a password for application in clicking this tab **Password application** like below
![Screenshot from 2023-03-03 16-01-50](https://user-images.githubusercontent.com/58255353/222754239-b2fdb9cf-22df-42fc-8348-1bbdf064b8b0.png)
7. Following the step 
8. Select the other application, put a name of your application
![Screenshot from 2023-03-03 16-05-23](https://user-images.githubusercontent.com/58255353/222754842-c280d4bb-df38-4386-9000-d12e6aa2fa18.png)
9. Save the password for the application 
![Screenshot from 2023-03-03 16-05-37](https://user-images.githubusercontent.com/58255353/222754939-3ca1da48-ea4b-4094-8f1f-63b34d824642.png)
10. You can send an email with the goraphe application

### Installation
1. Clone the repository
```bash
git clone https://github.com/ESIPE-Way2Go/Goraphe.git
```
2. Go to root of the project
3. Create a file environnement called .env in the root repository of project like as following
```
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/goraphe
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
SPRING_DATASOURCE_SSL=false

# Server
SPRING_SECURITY_SECRET=secret
SPRING_SECURITY_EXPIRATION_MS=86400000
SERVER_URL_DEV=http://localhost

# SMTP
SPRING_MAIL_USERNAME=url of your google account
SPRING_MAIL_PASSWORD=password of your google account (step STMP 10)
```
4. Execute the following command in terminal 
```
docker-compose up
```

4. If you turn the application in local, this one should now be running at http://localhost.

<p align="right">(<a href="#readme-top">back to top</a>)</p>



<!-- USAGE EXAMPLES -->
## Usage

Use this space to show useful examples of how a project can be used. Additional screenshots, code examples and demos work well in this space. You may also link to more resources.

_For more examples, please refer to the [Documentation](https://example.com)_

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- LICENSE -->
## License
Distributed under the MIT License. See `LICENSE.txt` for more information.
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->
## Acknowledgments

Use this space to list resources you find helpful and would like to give credit to. I've included a few of my favorites to kick things off!

* [Choose an Open Source License](https://choosealicense.com)
* [GitHub Emoji Cheat Sheet](https://www.webpagefx.com/tools/emoji-cheat-sheet)
* [Malven's Flexbox Cheatsheet](https://flexbox.malven.co/)
* [Malven's Grid Cheatsheet](https://grid.malven.co/)
* [Img Shields](https://shields.io)
* [GitHub Pages](https://pages.github.com)
* [Font Awesome](https://fontawesome.com)
* [React Icons](https://react-icons.github.io/react-icons/search)

<p align="right">(<a href="#readme-top">back to top</a>)</p>
