# n FoodRescue
> ### n Turning Surplus Food into Social Impact
**FoodRescue** is a Java-based surplus food redistribution platform designed to connect **food providers,
NGOs, and volunteers** to reduce food waste and help deliver surplus food to people in need.
Instead of letting excess food go to waste, FoodRescue creates a structured digital platform where surplus
food can be **listed, discovered, claimed, and coordinated for pickup and redistribution**.
---
## n Why FoodRescue?
Every day, large amounts of edible food are wasted while many people struggle to access sufficient food.
FoodRescue aims to bridge this gap through technology.
### n Our Mission
> **Rescue food. Reduce waste. Rebuild communities.**
---
## n Key Features
- n User Registration & Login
- n Role-Based Authentication
- nn Food Provider Module
- n NGO Module
- n Volunteer Module
- n Surplus Food Listing
- n Food Claim & Pickup Workflow
- n Notifications
- nnn Admin Management
- n Food Redistribution Tracking
- n Secure Database Connectivity
---
## nn Technology Stack
| Technology | Purpose |
|---|---|
| n Java | Backend Development |
| n Jakarta Servlet | Web Request Handling |
| n JSP | Dynamic Web Pages |
| n HTML/CSS/JavaScript | Frontend |
| nn MySQL | Database |
| n JDBC | Database Connectivity |
| n MVC | Application Architecture |
| n Maven | Build & Dependency Management |
| n Apache Tomcat | Application Server |
| n Git & GitHub | Version Control |
---
## n Team FoodRescue
| Role | Team Member |
|---|---|
| n **Team Leader** | **Rajeev Kumar** |
| n **Team Member** | **Raunak Kumar** |
| n **Team Member** | **Riteek Kumar** |
| n **Team Member** | **Ritu Raj** |
> **One team. One mission. Less waste. More impact. n**
---
## n Project Structure
```text
FoodRescue/
n
nnn src/
n nnn main/
n nnn java/
n n nnn com/
n n nnn foodrescue/
n n nnn controller/
n n nnn dao/
n n nnn model/
n n nnn service/
n n nnn util/
n n
n nnn webapp/
n nnn WEB-INF/
n
nnn pom.xml
nnn README.md
nnn .gitignore
```
---
## nn Getting Started
### 1nn Clone the Repository
```bash
git clone https://github.com/YOUR-USERNAME/FoodRescue.git
```
### 2nn Navigate to the Project
```bash
cd FoodRescue
```
### 3nn Configure MySQL
Create the database:
```sql
CREATE DATABASE foodrescue;
```
Configure your database credentials in the application's configuration.
> nn **Never commit your MySQL password, API keys, or other secrets to GitHub.**
### 4nn Build the Project
```bash
mvn clean package
```
### 5nn Deploy on Apache Tomcat
Deploy the generated WAR file:
```text
target/FoodRescue.war
```
to the Tomcat `webapps` directory.
### 6nn Run the Application
```text
http://localhost:8080/FoodRescue/
```
---
## n Food Redistribution Flow
```text
nn Food Provider
n
t
n Add Surplus Food
n
t
n NGO Discovers Food
n
t
n Claim Food
n
t
n Volunteer Pickup
n
t
n Food Delivered
n
t
¤n Social Impact
```
---
## n Social Impact
### nn Reduce Food Waste
Help prevent edible surplus food from being unnecessarily discarded.
### n Connect Communities
Create a bridge between food providers, NGOs, and volunteers.
### n Promote Sustainability
Encourage responsible food redistribution and contribute toward a more sustainable future.
---
## nn Security
- n Role-based authentication
- n Password protection
- nn Session management
- n Input validation
- n Secure database configuration
- n No sensitive credentials committed to GitHub
---
## nn Project Roadmap
- [x] Project Setup
- [x] Maven Configuration
- [x] Servlet & Tomcat Setup
- [x] JDBC Database Connectivity
- [ ] User Registration & Login
- [ ] Provider Module
- [ ] NGO Module
- [ ] Volunteer Module
- [ ] Food Claim System
- [ ] Pickup Management
- [ ] Notification System
- [ ] Admin Panel
- [ ] Testing
- [ ] Deployment
---
## n Contribution
```text
Create Branch
↓
Develop Feature
↓
Test Changes
↓
Commit
↓
Push Branch
↓
Pull Request
↓
Code Review
↓
Merge
```
Example:
```bash
git checkout -b feature/login
git add .
git commit -m "feat: add user login"
git push origin feature/login
```
---
## n License
This project is currently developed as an academic and social-impact project.
---
## n Our Vision
> ### "Technology should not only make life smarter — it should make society better."
**FoodRescue** aims to transform surplus food into opportunity, reduce unnecessary waste, and create a
stronger connection between communities.
---
# n FoodRescue
### n Rescue Food • Reduce Waste • Create Impact
**Built with ¤n by Team FoodRescue**
