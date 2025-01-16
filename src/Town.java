/**
 * The Town Class is where it all happens.
 * The Town is designed to manage all the things a Hunter can do in town.
 * This code has been adapted from Ivan Turner's original program -- thank you Mr. Turner!
 */

public class Town {
    // instance variables
    private Hunter hunter;
    private Shop shop;
    private Terrain terrain;
    private String printMessage;
    private boolean toughTown;
    private boolean alreadyDug;
    private String treasure;
    private boolean searched;
    private boolean easyMode;
    private boolean samuraiMode;

    /**
     * The Town Constructor takes in a shop and the surrounding terrain, but leaves the hunter as null until one arrives.
     *
     * @param shop The town's shoppe.
     * @param toughness The surrounding terrain.
     */
    public Town(Shop shop, double toughness, boolean easyMode, boolean samuraiMode) {
        this.samuraiMode = samuraiMode;
        this.easyMode = easyMode;
        this.shop = shop;
        this.terrain = getNewTerrain();

        // the hunter gets set using the hunterArrives method, which
        // gets called from a client class
        hunter = null;
        printMessage = "";

        // higher toughness = more likely to be a tough town
        toughTown = (Math.random() < toughness);

        alreadyDug = false;
        String[] treasureList = {"a crown", "a trophy", "a gem", "dust"};
        int random = (int) (Math.random() * 3);
        treasure = treasureList[random];
        searched = false;
    }

    public void treasureHunt(){
        boolean containsTreasure = false;
        for(int i = hunter.getCollectedTreasure().length - 1; i > -1 ; i--){
            if(hunter.getCollectedTreasure()[i] == null){
                containsTreasure = false;
            } else if(hunter.getCollectedTreasure()[i].equals(treasure)){
                containsTreasure = true;
            }

        }
        if(containsTreasure){
            printMessage = "You found a " +treasure+ " but you already have this treasure ";
        }
        if(searched){
            printMessage = "You have already searched this town.";
        }
        else if (!containsTreasure ){
            if(!treasure.equals("dust")){
                printMessage = "You found " + treasure +"!";
                hunter.addCollectedTreasure(treasure);
            }
            searched = true;
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public String getLatestNews() {
        return printMessage;
    }

    /**
     * Assigns an object to the Hunter in town.
     *
     * @param hunter The arriving Hunter.
     */
    public void hunterArrives(Hunter hunter) {
        this.hunter = hunter;
        printMessage = "Welcome to town, " + hunter.getHunterName() + ".";
        if (toughTown) {
            printMessage += "\nIt's pretty rough around here, so watch yourself.";
        } else {
            printMessage += "\nWe're just a sleepy little town with mild mannered folk.";
        }
    }

    /**
     * Handles the action of the Hunter leaving the town.
     *
     * @return true if the Hunter was able to leave town.
     */
    public boolean leaveTown() {
        boolean canLeaveTown = terrain.canCrossTerrain(hunter);
        if (canLeaveTown) {
            String item = terrain.getNeededItem();
            printMessage = "You used your " + item + " to cross the " + terrain.getTerrainName() + ".";
            if (checkItemBreak() && !easyMode) {
                hunter.removeItemFromKit(item);
                printMessage += "\nUnfortunately, you lost your " + item;
            }
            String[] treasureList = {"a crown", "a trophy", "a gem", "dust"};
            int random = (int) (Math.random() * 3);
            treasure = treasureList[random];
            searched = false;
            return true;
        }

        printMessage = "You can't leave town, " + hunter.getHunterName() + ". You don't have " + terrain.getNeededItem() + ".";
        return false;
    }

    /**
     * Handles calling the enter method on shop whenever the user wants to access the shop.
     *
     * @param choice If the user wants to buy or sell items at the shop.
     */
    public void enterShop(String choice) {
        printMessage = shop.enter(hunter, choice);
    }

    /**
     * Gives the hunter a chance to fight for some gold.<p>
     * The chances of finding a fight and winning the gold are based on the toughness of the town.<p>
     * The tougher the town, the easier it is to find a fight, and the harder it is to win one.
     */
    public void lookForTrouble() {
        double noTroubleChance;
        if (toughTown) {
            noTroubleChance = 0.66;
        } else {
            noTroubleChance = 0.33;
        }
        if (Math.random() > noTroubleChance) {
            printMessage = "You couldn't find any trouble";
        } else {
            double loseChance = noTroubleChance;
            if (easyMode){
                loseChance = 0.1;
            } else if (samuraiMode){
                loseChance = 0;
            }
            printMessage = Colors.RED + "You want trouble, stranger!  You got it!\nOof! Umph! Ow!\n";
            int goldDiff = (int) (Math.random() * 10) + 1;
            if (Math.random() > loseChance) {
                if (hunter.hasItemInKit("sword")){
                    printMessage += "AAAHH! they saw your mighty sword and got scared";
                }else{
                    printMessage += "Okay, stranger! You proved yer mettle. Here, take my gold.";
                }
                printMessage += "\nYou won the brawl and receive " + Colors.YELLOW + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(goldDiff);
            } else {
                printMessage += "That'll teach you to go lookin' fer trouble in MY town! Now pay up!";
                printMessage += "\nYou lost the brawl and pay " + goldDiff + " gold." + Colors.RESET;
                hunter.changeGold(-goldDiff);
            }
        }
    }

    public String infoString() {
        return "This nice little town is surrounded by a " + terrain.getTerrainName() + ".";
    }

    /**
     * Determines the surrounding terrain for a town, and the item needed in order to cross that terrain.
     *
     * @return A Terrain object.
     */
    private Terrain getNewTerrain() {
        double rnd = Math.random() + 0.2;
        if (rnd < .2) {
            return new Terrain("Mountains", "Rope");
        } else if (rnd < .4) {
            return new Terrain("Ocean", "Boat");
        } else if (rnd < .6) {
            return new Terrain("Plains", "Horse");
        } else if (rnd < .8) {
            return new Terrain("Desert", "Water");
        } else if (rnd < 1.0){
            return new Terrain("Marsh", "Boot");
        } else {
            return new Terrain("Jungle", "Machete");
        }
    }

    /**
     * Determines whether a used item has broken.
     *
     * @return true if the item broke.
     */
    private boolean checkItemBreak() {
        double rand = Math.random();
        return (rand < 0.5);
    }

    public void dig(){
        if (!hunter.hasItemInKit("shovel")){
            printMessage = "You can't dig for gold without a shovel";
        } else if (alreadyDug){
           printMessage = "You already dug for gold in this town";
        } else{
            alreadyDug = true;
            double rand = Math.random();
            if (rand >0.49){
                int extra = (int) (Math.random()*20) + 1;
                printMessage = "You dug up " + extra + " gold";
                hunter.changeGold(extra);
            }else {
                printMessage = "You dug but only found dirt";
            }

        }

    }
}