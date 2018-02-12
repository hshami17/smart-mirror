default: all

DEPLOY_DIR=SMART_MIRROR

all:
	mvn install; \
	mkdir -p $(DEPLOY_DIR)/resources; \
	rsync -rv target/smart-mirror-1.0.jar $(DEPLOY_DIR); \
	rsync -rv scripts/ $(DEPLOY_DIR); \
	rsync -rv src/main/resources/mirror_config.xml $(DEPLOY_DIR)/resources; \
	rsync -av web-service $(DEPLOY_DIR); \
	echo 'Directory created on:' > $(DEPLOY_DIR)/info.txt & \
	date >> $(DEPLOY_DIR)/info.txt


dist:
	make; \
	tar cvmf smartmirror.tar.gz $(DEPLOY_DIR); 

clean:
	mvn clean; \
	rm -rf $(DEPLOY_DIR); \
	rm -rf smartmirror.tar.gz;
