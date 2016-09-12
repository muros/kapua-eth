# Manually Running the Development Machine
The development machine contains all the external services needed for running a full instance of Kapua.

* ActiveMQ
* H2 Database
* Elasticsearch
* Tomcat

The development machine is a Vagrant machine that is based on a local vagrant box named trusty64/kapua-dev-box-0.x. Before running the development machine it's necessary to create a local base box.

**Note:** Check that Vagrant is installed in your local machine, otherwise install it. In order to run Vagrant machines VirtualBox or other virtualization software supported by Vagrant needs also to be installed.

You can check if there's already the vagrant box with the command:

* $ sh ./vagrant box list

If the box is not present it can be created running the script:

* create_kapua_box.sh

If the box needs to be created you have to remove the box from the local repo and then create it again:

1. $ vagrant box remove trusty64/kapua-dev-box-0.x
2. $ sh ./vagrant box list

The script uses the machine definition in Kapua-Box-Vagrantfile to add a local box named:

* trusty64/kapua-dev-box-0.x

the box is created in the local Vagrant repository. 

**Note:** The creation of the vagrant box requires a connection to the internet since it downloads the software for running the services live.

Once the box creation has been completed, you can run the development machine by executing the command:

* $ vagrant up

The machine is starts and the services are launched. The machine is assigned the following IP:

* 192.168.33.10

The ports exposed are documented per service wihtin the Vagran file.





 