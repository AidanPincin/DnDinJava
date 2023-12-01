import java.util.*;
public class DnD{
    static int getConsoleWidth(){
        try{
            Process process = new ProcessBuilder("sh", "-c", "tput cols").start();
            String result = new String(process.getInputStream().readAllBytes());
            return Integer.parseInt(result.trim());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return 80;
    }

    static void print(Object text) throws InterruptedException{
        int consoleWidth = getConsoleWidth();
        ArrayList<String> lines = new ArrayList<String>();
        int indexOfLastSpace = 0;
        int indexOfFirstSpace = 0;
        while(true){
            if(text.toString().substring(indexOfFirstSpace,text.toString().length()).length() > consoleWidth){
                for(int i=indexOfFirstSpace; i<consoleWidth+indexOfFirstSpace; i++){
                    if(text.toString().substring(i, i+1).equals(" ")){
                        indexOfLastSpace = i+1;
                    }
                }
                lines.add(text.toString().substring(indexOfFirstSpace,indexOfLastSpace));
                indexOfFirstSpace = indexOfLastSpace;
            }
            else{
                lines.add(text.toString().substring(indexOfLastSpace,text.toString().length()));
                break;
            }
        }
        for(int line=0; line<lines.size(); line++){
            for(int i=0; i<lines.get(line).length(); i++){
                System.out.print(lines.get(line).charAt(i));
                Thread.sleep(15);
            }
            System.out.println();
        }
    }

    static Scanner input = new Scanner(System.in);
    static class DnDCharacter{
        Map<String, Object> variableMap = new HashMap<>();
        DnDCharacter(){
            variableMap.put("strength", 0);
            variableMap.put("dexterity", 0);
            variableMap.put("intelligence", 0);
            variableMap.put("constitution", 0);
            variableMap.put("wisdom", 0);
            variableMap.put("perception", 0);
            variableMap.put("charisma", 0);
            variableMap.put("hp", 0);
            variableMap.put("max_hp", 0);
            variableMap.put("level", 1);
            variableMap.put("xp", 0);
            variableMap.put("max_xp", 50);
            variableMap.put("gold", 0);
            variableMap.put("name", "");
        }
        Object getVariable(String variableName){
            return variableMap.get(variableName);
        }
        void setVariable(String variableName, Object newValue){
            variableMap.put(variableName, newValue);
        }
        void printVariables() throws InterruptedException{
            String stats[] = {"Name","Gold","Level","Strength","Dexterity","Intelligence","Constitution","Wisdom","Perception","Charisma"};
            for(int i=0; i<10; i++){
                print(stats[i]+" : "+getVariable(stats[i].toLowerCase()));
                if(i==0){
                    print("HP"+" : "+getVariable("hp")+"/"+getVariable("max_hp"));
                }
                if(i==2){
                    print("XP : "+getVariable("xp")+"/"+getVariable("max_xp"));
                }
            }
        }
    }
    static DnDCharacter character = new DnDCharacter();

    private static int[] rollDice(int numDice, int sides){
        int[] roll = new int[numDice];
        for(int i=0; i<numDice; i++){
            roll[i] = (int)Math.ceil(Math.random()*sides);
        }
        return roll;
    }

    static void createCharacterName() throws InterruptedException{
        boolean isValidName = false;
        while(!isValidName){
            print("Name must be in between 3 and 18 characters and may not include special characters except spaces, apostrophes, hyphons, underscores, and periods but may not contain more than 3 in total.");
            isValidName = true;
            int specialCharacterCount = 0;
            String name = input.nextLine();
            if(name.length()>=3 && name.length()<=18){
                for(int i=0; i<name.length(); i++){
                    if(!Character.isLetterOrDigit(name.charAt(i))){
                        isValidName = false;
                        char[] specialCharacters = {'\'', '.', '_', '-', ' '};
                        for(int o=0; o<specialCharacters.length; o++){
                            if(name.charAt(i) == specialCharacters[o]){
                                specialCharacterCount++;
                                isValidName = true;
                            }
                        }
                    }
                }
            }
            if(specialCharacterCount>3){
                isValidName = false;
            }
            if(isValidName){
                while(true){
                    print("Are you sure you want to be name "+'"'+name+'"'+"? You will not be able to change your name('y' for yes and 'n' for no)");
                    String confirm = input.nextLine();
                    if(confirm.equals("y")){
                        character.setVariable("name", name);
                        print("Great! Let's move on to the next step "+name);
                        break;
                    }
                    else if(confirm.equals("n")){
                        isValidName = false;
                        break;
                    }
                }
            }
        }
    }

    static void GettingStarted() throws InterruptedException{
        print("Welcome to Dungeon Adventure!");
        print("What would you like your character name to be?");
        createCharacterName();
        createCharacterAttributes();
    }

    static void createCharacterAttributes() throws InterruptedException{
        String[] attributes = {"strength","dexterity","intelligence","constitution","wisdom","perception","charisma"};
        print("Now we will begin assigning your attributes. You have 7 attributes which are strength, dexterity, intelligence, constitution, wisdom, perception, and charisma. To determine each attribute value I will roll 4 6-sided dice and sum up the 3 highest rolls which you will get to assign to one of your unassigned attributes.(press enter to continue)");
        input.nextLine();
        for(int i=0; i<6; i++){
            int[] roll = rollDice(4, 6);
            int sum = Math.max(roll[0],roll[1])+Math.max(roll[2],roll[3])+Math.max(Math.min(roll[0],roll[1]),Math.min(roll[2],roll[3]));
            print("Rolling the dice you got "+roll[0]+", "+roll[1]+", "+roll[2]+", and "+roll[3]+". So the 3 highest rolls add up to a toal of "+sum);
            print("Your remaining attributes are:");
            while(true){
                for(int a=0; a<7; a++){
                    if((int)character.getVariable(attributes[a]) == 0){
                        print(attributes[a]);
                    }
                }
                print("Which attribute would you like to assign the value of "+sum+"?");
                String attribute = input.nextLine().toLowerCase();
                if(Arrays.asList(attributes).contains(attribute) && (int)character.getVariable(attribute) == 0){
                    character.setVariable(attribute, sum);
                    print("Your "+attribute+" has been assigned the value of "+sum);
                    break;
                }
                else{
                    print("You must assign the value to one fo the following attributes");
                }
            }
        }
        String remainingAttribute = "";
        int[] roll = rollDice(4, 6);
        int sum = Math.max(roll[0],roll[1])+Math.max(roll[2],roll[3])+Math.max(Math.min(roll[0],roll[1]),Math.min(roll[2],roll[3]));
        print("Rolling the dice you got "+roll[0]+", "+roll[1]+", "+roll[2]+", and "+roll[3]+". So the 3 highest rolls add up to a toal of "+sum);
        for(int a=0; a<7; a++){
            if((int)character.getVariable(attributes[a]) == 0){
                remainingAttribute = attributes[a];
                character.setVariable(remainingAttribute, sum);
            }
        }
        print("So your remaining attribute "+remainingAttribute+" has been assigned the value of "+sum+"(press Enter to continue)");
        input.nextLine();
        print("Now I must determine your starting hitpoints by multiplying your constitution by 2 and adding the sum of 2 six-sided dice(press Enter to continue)");
        input.nextLine();
        roll = rollDice(2, 6);
        sum = roll[0]+roll[1];
        character.setVariable("hp", (int)character.getVariable("constitution")*2+sum);
        character.setVariable("max_hp", character.getVariable("hp"));
        print("Rolling the dice you got "+roll[0]+" and "+roll[1]+" for a toal of "+sum+" plus your constitution of "+character.getVariable("constitution")+" multiplied by 2 gets you a grand total of "+character.getVariable("hp")+". So you will start with "+character.getVariable("hp")+" hit points(press Enter to continue)");
        input.nextLine();
        roll = rollDice(20,6);
        sum = 0;
        for(int i=0; i<20; i++){
            sum+=roll[i];
        }
        character.setVariable("gold", sum);
        print("Finally to determine your starting gold I will sum up the roll of 20 6-sided dice. Rolling the dice you got a grand total of "+character.getVariable("gold")+" gold to start with(press Enter to continue)");
        input.nextLine();
        print("Congratulations! You have finished creating your character. Here are your stats: ");
        character.printVariables();
        print("(press Enter to continue)");
        input.nextLine();
    }

	public static void main(String[] args) throws InterruptedException{
        GettingStarted();
    }
}