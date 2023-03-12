<div align="center">
  <a href="https://github.com/ESIPE-Way2Go/Goraphe">
    <img src="/frontend/src/assets/Goraphe_small_no_text.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">Goraphe</h3>

  <p align="center">
    This project is a web application web which execute a <b>python script</b> in order to analyze the impact of events (accidents, works, speed limit reduction) on roads with the params selected by the user. 
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
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project
![Screenshot from 2023-03-03 15-35-27](https://user-images.githubusercontent.com/58255353/222748570-a90337ce-0c46-4b6a-8119-e3369e416718.png)

The purpose of the highway project is to create a software that allows the observation of
variations in travel time as a function of events (accidents, road works, speed limit reduction)
speed limit) occurring on the roads, which is the subject of research
of our client. The research papers that were produced by the client provide
many formulas on which we rely in our project.

### Built With

This project was built with the tehchnologies next
- [Docker](https://www.docker.com/)
- [Python](https://www.python.org/)
- [Vue](https://vuejs.org/)
- [Java](https://www.java.com/en/)
- [PostgreSQL](https://www.postgresql.org/)
- [Maven](https://maven.apache.org/)

## Getting-started

### Prerequisites
#### Docker
1. Install [docker](https://docs.docker.com/desktop/)

### Linux distribution for WSL (Windows Subsystem for Linux)
1. Go to the Miscrosoft Store and search "Ubuntu"
2. Click to install it :

![Capture d’écran 2023-03-12 182059](https://user-images.githubusercontent.com/117312169/224561561-e3da476c-bb59-43a7-bfc2-17da54bf9e88.jpg)

3. You will have to enter a username and a password to create a UNIX user
4. Click on Windows+R and type "cmd" then click on "Ok"

![Capture d’écran 2023-03-12 183039](https://user-images.githubusercontent.com/117312169/224561882-952f625e-0381-4ca5-b581-7ef98fd2df3c.jpg)

5.Type "wsl --set-default ubuntu" and enter

![Capture d’écran 2023-03-12 183153](https://user-images.githubusercontent.com/117312169/224561951-fb1d2e8f-5b9a-41e9-a03a-df85b5331553.jpg)

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

#### Get api key


### Installation
1. Clone the repository
```bash
git clone https://github.com/ESIPE-Way2Go/Goraphe.git
```
Or download the zip of the project with the user interface.
The project MUST be placed in the WSL2 in order for it to work properly in the directory **/home/name_of_user/**

![image](https://user-images.githubusercontent.com/77906813/223176368-ca90f455-2870-4401-b730-7304ffbdad99.png)

2. Go to root of the project
3. There is a user created with the script in **bdd** repository. This user is **admin** and password is **0000**. To change password, you must encrypt your new password [bcrypt](https://www.bcrypt.fr/) and update the **start.sql**.
4. Change the request with new ones below
```sql
INSERT INTO public."user"(username, password, email, role) VALUES ('admin', '{bcrypt}your password', 'email', 'ROLE_ADMIN');
```
:warning: don't change the **ROLE_ADMIN** for **admin role**

5. Create a file environnement called **.env** in the root repository of project like as following
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

6. Execute the following command in terminal (WSL)
```
docker-compose up
```
7. If you turn the application in local, this one should now be running at http://localhost.

### Remarque
As our project execute a python script, you can change the script python **without restart the container**. There is a share folder with the container dockers nominate **scripts**. You have to create a **scripts** repository on your machine at the root of the project.

## USAGE

### Simulation
1. Go to the url : http://localhost
2. Identifiy you with this admin account.
3. You can launch in new simulation with the button **Nouvelle simulation**
4. Selected two points on the map
![Screenshot from 2023-03-03 16-28-54](https://user-images.githubusercontent.com/58255353/222760190-bf126bef-eb5f-4cd0-baac-686be7195b47.png)
5. Fill the form
6. Launch simulation
7. You arrive on the new page where you see the progress of your launched simulation
8. Upon the simulation is finished, you can see the result on the map 
![Screenshot from 2023-03-03 16-31-44](https://user-images.githubusercontent.com/58255353/222760817-92eef043-cb5a-47a2-bb98-be82e2220e67.png)

### Administration
1. Go to the administration 
![Screenshot from 2023-03-03 16-48-09](https://user-images.githubusercontent.com/58255353/222764654-6530a545-0df9-4045-96f7-c9302983e30a.png)
2. To create account
3. You send an invitation 
4. These person receive an email to create is account
5. Go on the link 
6. Fill the form
7. Account created

<!-- LICENSE -->
## License
Distributed under the MIT License. See `LICENSE.txt` for more information.
