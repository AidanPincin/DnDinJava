import java.util.*;
import java.util.function.Consumer;
public class DnD{
    // Gets the width of the console that the program is running on(This may not work on all operating systems)
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

    // Prints text character by character to give the feeling of a narrator typing
    static void print(Object text) throws InterruptedException{
        int consoleWidth = getConsoleWidth();
        // Array of the lines that the String will be printed on
        ArrayList<String> lines = new ArrayList<String>();
        int indexOfLastSpace = 0;
        int indexOfFirstSpace = 0;
        // Determines where the string gets cut off in the console using consoleWidth and measuring string length
        // and and adds the substring from the start to the last word that doesnt get cutoff to the lines
        // array and repeats this process until no cutoffs are detected
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
        // Go through each line in the lines array and print character by character
        for(int line=0; line<lines.size(); line++){
            for(int i=0; i<lines.get(line).length(); i++){
                System.out.print(lines.get(line).charAt(i));
                Thread.sleep(15);
            }
            System.out.println();
        }
    }

    static Scanner input = new Scanner(System.in);
    // Character class that will be used to store all data relating to the user/player
    static class DnDCharacter{
        // Variable map that stores any kind of variable
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
            variableMap.put("location", "town");
            variableMap.put("tutorialMode", false);
            variableMap.put("inventory", new Item[25]);
            variableMap.put("weapon", new Item("fists",1,3,0));
            variableMap.put("armor", new Item("body",8,0));
        }
        Object getVariable(String variableName){
            return variableMap.get(variableName);
        }
        void setVariable(String variableName, Object newValue){
            variableMap.put(variableName, newValue);
        }
        void printStats() throws InterruptedException{
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
        // Returns the bonus of a specific attribute depending on the value of the attribute which will be used in a addition to a roll
        int getAttributeBonus(String attribute){
            int value = (int)getVariable(attribute);
            if(value>=16){return 3;}
            else if(value>=14){return 2;}
            else if(value>=12){return 1;}
            else if (value>=10){return 0;}
            else if(value>=8){return -1;}
            else if(value>=6){return -2;}
            else{return -3;}
        }
    }
    // Item class with overloading to determine if item is weapon, armor, potion, or spell
    static class Item{
        // Variable map that stores any type of variable
        Map<String, Object> variableMap = new HashMap<>();
        // weapon
        Item(String initName, int initNumOfDice, int initNumOfSides, int initCost){
            variableMap.put("type", "weapon");
            variableMap.put("name", initName);
            variableMap.put("numOfDice", initNumOfDice);
            variableMap.put("numOfSides", initNumOfSides);
            variableMap.put("cost", initCost);
        }
        // armor
        Item(String initName, int initArmorClass, int initCost){
            variableMap.put("type", "armor");
            variableMap.put("name", initName);
            variableMap.put("armorClass", initArmorClass);
            variableMap.put("cost", initCost);
        }
        // potion
        Item(String initName, String initDescription, int initCost, int amount, Consumer<Item> function){
            variableMap.put("type", "potion");
            variableMap.put("amount", amount);
            variableMap.put("name", initName);
            variableMap.put("cost", initCost);
            variableMap.put("description", initDescription);
            variableMap.put("function", function);
        }
        // spell
        Item(String initName, int initNumOfDice, int initNumOfSides, int initCost, int amount){
            variableMap.put("type", "spell");
            variableMap.put("amount", amount);
            variableMap.put("name", initName);
            variableMap.put("numOfDice", initNumOfDice);
            variableMap.put("numOfSides", initNumOfSides);
            variableMap.put("cost", initCost);
        }
        Object getVariable(String variableName){
            return variableMap.get(variableName);
        }
        // Prints the information of the item which is used when displaying items in the shop or displaying player inventory
        void printItem() throws InterruptedException{
            String name = (String)variableMap.get("name");
            int cost = (int)variableMap.get("cost");
            String info;
            String type = (String)variableMap.get("type");
            if(type.equals("weapon") || type.equals("spell")){
                info = "Damage : "+variableMap.get("numOfDice")+"-"+(int)variableMap.get("numOfDice")*(int)variableMap.get("numOfSides");
            }
            else if(type.equals("potion")){
                info = (String)variableMap.get("description");
            }
            else{
                info = "Armor Class : "+variableMap.get("armorClass");
            }
            print(name+" -- "+info+" || Cost : "+cost+" gold");
        }
        // Some items will have a stored function as one of its variables. This grabs the function and executes it when called
        void executeStoredFunction(){
            Consumer<Item> function = (Consumer<Item>) variableMap.get("function");
            function.accept(this);
        }
    }
    // Shop with initialization of all the items catorgized into weapons, armors, potions, and spells
    static class Shop{
        // Variable map that only stores 'Item' type arrays
        Map<String, Item[]> variableMap = new HashMap<>();
        Shop(){
            variableMap.put("weapons", new Item[]{
                new Item("Dagger",1,6,5),
                new Item("Sharp Sword",1,10,10),
                new Item("Morningstar",2,6,25),
                new Item("Long Sword",3,6,60),
                new Item("Battleaxe",4,6,125),
                new Item("Great Sword",5,6,300)
            });
            variableMap.put("armors", new Item[]{
                new Item("Cloth Armor",10,30),
                new Item("Leather Armor",11,60),
                new Item("Studded Leather Armor",12,90),
                new Item("Scale Mail Armor",13,200),
                new Item("Chain Mail Armor",14,400),
                new Item("Plate Mail Armor",15,800)
            });
            variableMap.put("spells", new Item[]{
                new Item("Magic Missle",3,6,12,5)
            });
            variableMap.put("potions", new Item[]{
                // Passing a function as a parameter in Java!                                                   ↓
                new Item("Healing Potion","Heals for : 5-30hp",15,3, item -> {
                    int[] roll = rollDice(5,6);
                    int total = roll[0]+roll[1]+roll[2]+roll[3]+roll[4]+character.getAttributeBonus("constitution");
                    character.setVariable("hp", total);
                    if((int)character.getVariable("hp") > (int)character.getVariable("max_hp")){
                        character.setVariable("hp", character.getVariable("max_hp"));
                    }
                    try {
                        print("Rolling the dice you got "+roll[0]+", "+roll[1]+", "+roll[2]+", "+roll[3]+", and "+roll[4]+" plus your constitution of "+character.getVariable("constitution")+" adds a bonus of "+character.getAttributeBonus("constitution")+" for a total of "+total+". So you heal for "+total+" hit points and you now have "+character.getVariable("hp")+" hitpoints remaining");
                    } catch (InterruptedException e) {}
                })
            });
        }
        // List all the items in the type(weapons, armors, potions, spells) for sale and their information
        void listItemsForSale(String type) throws InterruptedException{
            Object[] items = variableMap.get(type);
            for(int i=0; i<items.length; i++){
                variableMap.get(type)[i].printItem();
            }
        }
    }
    
    static DnDCharacter character = new DnDCharacter();
    static Shop shop = new Shop();

    // Simple roll dice function that returns an array of int given number of dice and number of sides on the dice
    private static int[] rollDice(int numDice, int sides){
        int[] roll = new int[numDice];
        for(int i=0; i<numDice; i++){
            roll[i] = (int)Math.ceil(Math.random()*sides);
        }
        return roll;
    }

    // Getting the user to create their name
    static void createCharacterName() throws InterruptedException{
        boolean isValidName = false;
        while(!isValidName){
            print("Name must be in between 3 and 18 characters and may not include special characters except spaces, apostrophes, hyphons, underscores, and periods but may not contain more than 3 in total.");
            isValidName = true;
            int specialCharacterCount = 0;
            String name = input.nextLine();
            // Checking to make sure name follows paremeters given
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
            // Verifying that the user wants to assign their name
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
                    else{
                        print("Invalid input");
                    }
                }
            }
        }
    }

    static void gettingStarted() throws InterruptedException{
        print("Welcome to Dungeon Adventure!");
        print("What would you like your character name to be?");
        createCharacterName();
        createCharacterAttributes();
    }
    // User creates their character, assigning their attribute values to whatever they roll one at a time
    static void createCharacterAttributes() throws InterruptedException{
        String[] attributes = {"strength","dexterity","intelligence","constitution","wisdom","perception","charisma"};
        print("Now we will begin assigning your attributes. You have 7 attributes which are strength, dexterity, intelligence, constitution, wisdom, perception, and charisma. To determine each attribute value I will roll 4 6-sided dice and sum up the 3 highest rolls which you will get to assign to one of your unassigned attributes.(press enter to continue)");
        input.nextLine();
        // Rolls dice and asks user to assign value 6 times
        for(int i=0; i<6; i++){
            // Rolls dice and finds sum of 3 highest values out of 4 dice
            int[] roll = rollDice(4, 6);
            int sum = Math.max(roll[0],roll[1])+Math.max(roll[2],roll[3])+Math.max(Math.min(roll[0],roll[1]),Math.min(roll[2],roll[3]));
            print("Rolling the dice you got "+roll[0]+", "+roll[1]+", "+roll[2]+", and "+roll[3]+". So the 3 highest rolls add up to a toal of "+sum);
            print("Your remaining attributes are:");
            while(true){
                for(int a=0; a<7; a++){
                    // Prints attributes that havent been assigned a value
                    if((int)character.getVariable(attributes[a]) == 0){
                        print(attributes[a]);
                    }
                }
                print("Which attribute would you like to assign the value of "+sum+"?");
                String attribute = input.nextLine().toLowerCase();
                // Check to make sure user entered a valid attribute that hasnt already been assigned
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
        // Automatically rolls for and assigns value for remaining attribute
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
        // Determines user's starting hit points
        print("Now I must determine your starting hitpoints by multiplying your constitution by 2 and adding the sum of 2 six-sided dice(press Enter to continue)");
        input.nextLine();
        roll = rollDice(2, 6);
        sum = roll[0]+roll[1];
        character.setVariable("hp", (int)character.getVariable("constitution")*2+sum);
        character.setVariable("max_hp", character.getVariable("hp"));
        print("Rolling the dice you got "+roll[0]+" and "+roll[1]+" for a toal of "+sum+" plus your constitution of "+character.getVariable("constitution")+" multiplied by 2 gets you a grand total of "+character.getVariable("hp")+". So you will start with "+character.getVariable("hp")+" hit points(press Enter to continue)");
        input.nextLine();
        // Determines user's starting gold
        roll = rollDice(20,6);
        sum = 0;
        for(int i=0; i<20; i++){
            sum+=roll[i];
        }
        character.setVariable("gold", sum);
        print("Finally to determine your starting gold I will sum up the roll of 20 6-sided dice. Rolling the dice you got a grand total of "+character.getVariable("gold")+" gold to start with(press Enter to continue)");
        input.nextLine();
        print("Congratulations! You have finished creating your character. Here are your stats: ");
        character.printStats();
        print("(press Enter to continue)");
        input.nextLine();
    }
    // Commands that can be used anywhere at anytime(in town, shop, dungeon, in the middle of a battle, etc.)
    static boolean worldCommands(String answer) throws InterruptedException{
        // Prints player inventory
        if(answer.contains("inventory")){
            print("Here is what you have in your inventory:");
            Item[] inventory = (Item[])character.getVariable("inventory");
            for(int i=0; i<inventory.length; i++){
                if(inventory[i] != null){
                    inventory[i].printItem();
                }
            }
            return true;
        }
        // Prints player stats
        if(answer.contains("stat")){
            print("Here are your stats: ");
            character.printStats();
        }
        // Lists commands that user can use depending on their location
        else if(answer.contains("help")){
            String characterLocation = (String)character.getVariable("location");
            if(characterLocation.equals("town")){
                print("Some commands you can say are: \n 'go to shop' or 'go to dungeon'\n'view inventory'\n'view stats'");
            }
            else if(characterLocation.equals("shop")){
                print("Some commands you can say are: \n 'go to town' or 'go to dungeon'\n'view inventory'\n'view stats'\n'view (weapons, armors, spells, potions, or all items) for sale'\n'buy (item name)'");
            }
            else if(characterLocation.equals("dungeon")){
                //list dungeon commands
            }
        }
        return false;
    }
    // This is where the player will play the entirety of the game inside an infinite loop until they die
    static void game() throws InterruptedException{
        String answer;
        while(true){
            // Gets character location which determines the actions the player can take and what happens
            String characterLocation = (String)character.getVariable("location");
            // If player is in town they can go to shop or dungeon and more things in the future
            if(characterLocation.equals("town")){
                print("You are in town. What would you like to do?");
                answer = input.nextLine().toLowerCase();
                if(answer.contains("shop")){
                    character.setVariable("location", "shop");
                }
                else if(answer.contains("dungeon")){
                    print("Dungeon has not yet been implemented into the game");
                }
                else if(!worldCommands(answer)){
                    print("Invalid command. Say 'help' for a list of commands");
                }
            }
            // Dungeon has not been implented into the game
            else if(characterLocation.equals("dungeon")){
                print("You are in the dungeon. What would you like to do?");
            }
            // If player is in shop they can go to town or dungeon, list items for sale, buy items, and more in the future
            else if(characterLocation.equals("shop")){
                print("You are in the shop. What would you like to do?");
                answer = input.nextLine().toLowerCase();
                if(answer.contains("town")){
                    character.setVariable("location", "town");
                }
                else if(answer.contains("weapons")){
                    print("Here are the weapons for sale");
                    shop.listItemsForSale("weapons");
                }
                else if(answer.contains("armors")){
                    print("Here are the armors for sale");
                    shop.listItemsForSale("armors");
                }
                else if(answer.contains("spells")){
                    print("Here are the spells for sale");
                    shop.listItemsForSale("spells");
                }
                else if(answer.contains("potions")){
                    print("Here are the potions for sale");
                    shop.listItemsForSale("potions");
                }
                else if(answer.contains("all items")){
                    print("Here are the weapons for sale");
                    shop.listItemsForSale("weapons");
                    print("\nHere are the armors for sale");
                    shop.listItemsForSale("armors");
                    print("\nHere are the spells for sale");
                    shop.listItemsForSale("spells");
                    print("\nHere are the potions for sale");
                    shop.listItemsForSale("potions");
                }
                // Buying an item
                else if(answer.contains("buy")){
                    Object[] selectedItem = new Object[2];
                    String[] itemTypes = {"weapons","armors","spells","potions"};
                    // Checks if existing item exists in the user input
                    for(int i=0; i<itemTypes.length; i++){
                        Object[] items = shop.variableMap.get(itemTypes[i]);
                        for(int o=0; o<items.length; o++){
                            if(answer.contains(((String)shop.variableMap.get(itemTypes[i])[o].getVariable("name")).toLowerCase())){
                                selectedItem = new Object[]{itemTypes[i], o};
                            }
                        }
                    }
                    // If item was found in user input then asks for varification that user would like to purchase 
                    // the item then checks if user is able to afford it
                    if(selectedItem[0] != null){
                        int characterGold = (int)character.getVariable("gold");
                        Item item = shop.variableMap.get((String)selectedItem[0])[(int)selectedItem[1]];
                        int itemCost = (int)item.getVariable("cost");
                        if(characterGold>=itemCost){
                            print("Are you sure you would like to purchase the "+item.getVariable("name")+" for "+itemCost+" gold?(y to confirm)");
                            answer = input.nextLine();
                            // Adds item to character inventory
                            if(answer.equals("y")){
                                character.setVariable("gold", characterGold-itemCost);
                                Item[] characterInventory = (Item[])character.getVariable("inventory");
                                for(int i=0; i<(int)characterInventory.length; i++){
                                    if(characterInventory[i] == null){
                                        characterInventory[i] = item;
                                        break;
                                    }
                                }
                                character.setVariable("inventory", (Item[])characterInventory);
                            }
                            else{
                                print("Your purchase has been cancelled");
                            }
                        }
                        else{
                            print("You do not have enough gold to purchase this item. You have "+characterGold+" gold and the item cost "+itemCost+" gold");
                        }
                    }
                    else{
                        print("That item does not exist. Please provide the item you would like to buy");
                    }
                }
                else if(answer.contains("dungeon")){
                    print("Dungeon has not yet been implemented into the game");
                }
                else if(!worldCommands(answer)){
                    print("Invalid command. Say 'help' for a list of commands");
                }
            }
        }
    }
	public static void main(String[] args) throws InterruptedException{
        gettingStarted();
        game();
    }
}
