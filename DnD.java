import java.util.*;
import java.util.function.Consumer;
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
    static class Item{
        Map<String, Object> variableMap = new HashMap<>();
        Item(String initName, int initNumOfDice, int initNumOfSides, int initCost){
            variableMap.put("type", "weapon");
            variableMap.put("name", initName);
            variableMap.put("numOfDice", initNumOfDice);
            variableMap.put("numOfSides", initNumOfSides);
            variableMap.put("cost", initCost);
        }
        Item(String initName, int initArmorClass, int initCost){
            variableMap.put("type", "armor");
            variableMap.put("name", initName);
            variableMap.put("armorClass", initArmorClass);
            variableMap.put("cost", initCost);
        }
        Item(String initName, String initDescription, int initCost, int amount, Consumer<Item> function){
            variableMap.put("type", "potion");
            variableMap.put("amount", amount);
            variableMap.put("name", initName);
            variableMap.put("cost", initCost);
            variableMap.put("description", initDescription);
            variableMap.put("function", function);
        }
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
        void executeStoredFunction(){
            Consumer<Item> function = (Consumer<Item>) variableMap.get("function");
            function.accept(this);
        }
    }
    static class Shop{
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
        void listItemsForSale(String type) throws InterruptedException{
            Object[] items = variableMap.get(type);
            for(int i=0; i<items.length; i++){
                variableMap.get(type)[i].printItem();
            }
        }
    }
    
    static DnDCharacter character = new DnDCharacter();
    static Shop shop = new Shop();

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

    static void gettingStarted() throws InterruptedException{
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
        character.printStats();
        print("(press Enter to continue)");
        input.nextLine();
    }

    static boolean worldCommands(String answer) throws InterruptedException{
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
        if(answer.contains("stat")){
            print("Here are your stats: ");
            character.printStats();
        }
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
    static void game() throws InterruptedException{
        String answer;
        while(true){
            String characterLocation = (String)character.getVariable("location");
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
            else if(characterLocation.equals("dungeon")){
                print("You are in the dungeon. What would you like to do?");
            }
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
                else if(answer.contains("buy")){
                    Object[] selectedItem = new Object[2];
                    String[] itemTypes = {"weapons","armors","spells","potions"};
                    for(int i=0; i<itemTypes.length; i++){
                        Object[] items = shop.variableMap.get(itemTypes[i]);
                        for(int o=0; o<items.length; o++){
                            if(answer.contains(((String)shop.variableMap.get(itemTypes[i])[o].getVariable("name")).toLowerCase())){
                                selectedItem = new Object[]{itemTypes[i], o};
                            }
                        }
                    }
                    if(selectedItem[0] != null){
                        int characterGold = (int)character.getVariable("gold");
                        Item item = shop.variableMap.get((String)selectedItem[0])[(int)selectedItem[1]];
                        int itemCost = (int)item.getVariable("cost");
                        if(characterGold>=itemCost){
                            print("Are you sure you would like to purchase the "+item.getVariable("name")+" for "+itemCost+" gold?(y to confirm)");
                            answer = input.nextLine();
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
                        print("That item does not exist. Please tell me what you would like to buy");
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
