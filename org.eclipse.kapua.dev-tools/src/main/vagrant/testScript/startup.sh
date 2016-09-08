echo 'VARGRANT UP......'
vagrant box list | grep -q trusty64/kapua-dev-box-0.1 || ../create_kapua_box.sh
vagrant up
echo 'VARGRANT UP...... DONE'