./gradlew clean && ./gradlew build 
echo
echo "Compilation et exécution du programme prixttc.lbd"
java -jar build/libs/LambadaCompiler.jar prixttc.lbd > prixttc.asm && java -jar vm-0.9.jar prixttc.asm
echo
echo "Compilation et exécution du programme pgcd.lbd"
java -jar build/libs/LambadaCompiler.jar pgcd.lbd > pgcd.asm && java -jar vm-0.9.jar pgcd.asm
echo
echo "Compilation et exécution du programme factorial.lbd"
java -jar build/libs/LambadaCompiler.jar factorial.lbd > factorial.asm && java -jar vm-0.9.jar factorial.asm