package machine

import kotlin.math.abs

typealias typeCupVolume = Int
typealias volumeMap = Map<String, typeCupVolume>

fun getStdinInt(msg: String): Int {
    println(msg)
    return readln().toInt()
}

const val DEF_MACHINE_FORMAT_OUT = """%d ml of water
%d ml of milk
%d g of coffee beans"""

const val DEF_MACHINE_FORMAT_OUT_STATUS ="""The coffee machine has:
%d ml of water
%d ml of milk
%d g of coffee beans
%d disposable cups
$%d of money"""

data class coffeeStruct(val ml_water: Int, val mg_coffee: Int, val ml_milk: Int, val cost: Int)
data class NotEnoughSomeThing(var what:String): Exception("Not enough some") 

class machine(var avaiableWater: typeCupVolume = 400, var avaiableMilk: typeCupVolume = 540, var avaiableCoffee: typeCupVolume = 120, var `disposable cups`: Int = 9, var cash: Int = 0) {
    var m_coffeMap = mutableMapOf<String, coffeeStruct>()
    init {
    	m_coffeMap.put("espresso", coffeeStruct(ml_water = 250, mg_coffee = 16, ml_milk = 0, cost = 4))
    	m_coffeMap.put("latte", coffeeStruct(ml_water = 350, mg_coffee = 20, ml_milk = 75, cost = 7))
    	m_coffeMap.put("cappuccino", coffeeStruct(ml_water = 200, mg_coffee = 12, ml_milk = 100, cost = 6))
    }
    fun preCalculateCups(cups: typeCupVolume, coffeeType: coffeeStruct, writeAbout: Boolean = false): volumeMap  {
    	val fWater = cups * coffeeType.ml_water
    	val fMilk = cups * coffeeType.ml_milk
    	val fCoffee = cups * coffeeType.mg_coffee
    	if(writeAbout){
		println("For $cups cups of coffee you will need:")
		println(DEF_MACHINE_FORMAT_OUT.format(fWater, fMilk, fCoffee))
	}
	return mutableMapOf("water" to fWater, "milk" to fMilk, "coffee" to fCoffee)
    }
    
    fun calculateMaxCups(coffeeType: coffeeStruct): Int {
    	   try {
    	   	if (coffeeType.ml_milk == 0) return minOf((avaiableWater!! / coffeeType.ml_water),(avaiableCoffee!! / coffeeType.mg_coffee))
	   	return minOf((avaiableWater!! / coffeeType.ml_water), (avaiableMilk!! / coffeeType.ml_milk), (avaiableCoffee!! / coffeeType.mg_coffee))
	   } catch (_: java.lang.ArithmeticException) {
	   	return 0
	   }
    }
    
    fun printStatus() {
    	println(DEF_MACHINE_FORMAT_OUT_STATUS.format(avaiableWater!!, avaiableMilk!!, avaiableCoffee!!, `disposable cups`, cash))
    }
    
    fun giveCoffee(coffeeType: coffeeStruct) {
    	val m = preCalculateCups(1, coffeeType)
    	avaiableWater = avaiableWater!! - m["water"]!!
    	avaiableMilk = avaiableMilk!! - m["milk"]!!
    	avaiableCoffee = avaiableCoffee!! - m["coffee"]!!
    	cash += coffeeType.cost
    	`disposable cups`--
    }
    
    fun run() {
    	RunCycleLabel@while(true) {
    		try {
		    	println("Write action (buy, fill, take, remaining, exit): ")
		    	val action = readln()
		    	println()
		    	when (action) {
		    		"remaining" -> {
		    			printStatus()
		    		}
		    		"buy" -> {
		    			var listCoffee = m_coffeMap.toList() // Pair<String, Value>
		    			print("What do you want to buy? ")
		    			//var index = 1
		    			//var coffeeList = mutableListOf<Pair<Int, coffeeStruct>>()
		    			for (index in listCoffee.indices) { // we can to use m_coffeMap.keys instead
		    				//coffeeList.add(Pair(index, m_coffeMap[key]!!))    	
		    				var (k,_) = listCoffee[index]
		    				print("${index+1} - ${k}, ")
		    			}
		    			print(", back - to main menu:")
		    			println()
		    			//println(" 1 - espresso, 2 - latte, 3 - cappuccino: ")
		    			val x = readln()
		    			if (x == "back") continue
		    			val (_,ourCoffee) = listCoffee[(x.toInt()-1) % m_coffeMap.size]
		    			
		    			if (calculateMaxCups(ourCoffee) > 0) {
		    				giveCoffee(ourCoffee)
		    				println("I have enough resources, making you a coffee!")
		    			} else {
		    				if (avaiableWater!! == 0 || (ourCoffee.ml_water != 0 && avaiableWater!! / ourCoffee.ml_water < 1)) throw NotEnoughSomeThing("water")
		    				if (avaiableMilk!! == 0 || (ourCoffee.ml_milk != 0 && avaiableMilk!! / ourCoffee.ml_milk < 1)) throw NotEnoughSomeThing("milk")
		    				if (avaiableCoffee!! == 0 || (ourCoffee.mg_coffee != 0 && avaiableCoffee!! / ourCoffee.mg_coffee < 1)) throw NotEnoughSomeThing("coffee")
		    				if (`disposable cups` == 0) throw NotEnoughSomeThing("disposable cups")
		    			}
		    			
		    			// TODO("")
		    		}
		    		"fill" -> {
		    			val addWaterMl = getStdinInt("Write how many ml of water you want to add: ")
		    			val addMilkMl = getStdinInt("Write how many ml of milk you want to add: ")
		    			val addCoffeeGr = getStdinInt("Write how many grams of coffee beans you want to add: ")
		    			val addCups = getStdinInt("Write how many disposable cups you want to add: ")
		    			`disposable cups` += addCups
		    			avaiableCoffee = avaiableCoffee!! + addCoffeeGr
		    			avaiableMilk = avaiableMilk!! + addMilkMl
		    			avaiableWater = avaiableWater!! + addWaterMl
		    		}
		    		"take" -> {
		    			println("I gave you $$cash")
		    			cash = 0
		    		}
		    		"exit" -> {
		    			break
		    		}
		    	}
		   } catch (exc: NotEnoughSomeThing) {
		   	println("Sorry, not enough ${exc.what}!")
		   	//println("Please fill your coffee machine")
		   }
		   println()
	} // RunCycleLabel@
    }
}
val stages = """Starting to make a coffee
Grinding coffee beans
Boiling water
Mixing boiled water with crushed coffee beans
Pouring coffee into the cup
Pouring some milk into the cup
Coffee is ready!""".trim()
fun main() {
    val myMachine = machine(avaiableWater = 400, avaiableMilk = 540, avaiableCoffee = 120, cash = 550, `disposable cups` = 9)
    myMachine.run()
}

