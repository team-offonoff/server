mkdir -p deploy
cp build/libs/*.jar deploy/application.jar
cp Procfile deploy/Procfile
cp -r .ebextensions deploy/.ebextensions
cp -r .platform deploy/.platform
cd deploy && zip -r deploy.zip .