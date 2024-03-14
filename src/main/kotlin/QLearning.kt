package org.example
import kotlin.math.abs
import kotlin.random.Random

class QLearning(actionSpace: Int, private var worldDim: Int, defaultValue: Int, defaultState: String, private var alpha: Int) {
    // QTable representing QValues: Each row is a location and containes values for each action (U, D, L, R). Initialized to defaulValue
    private val qTable: MutableList<MutableList<Int>> = MutableList(worldDim*worldDim) { MutableList(actionSpace) { defaultValue } }

    // List of actions the mouse can make at any point (move up, down, left, right)
    private val actions = setOf("U","D","L","R")

    // Discount factor:
    private var gamma = 0.5

    // A 2D grid of the world initializing each square to the defaultState
    private val world = MutableList(worldDim) { MutableList(worldDim) { defaultState } }
    private val rewards = mutableMapOf("Cheese" to 2, "Cookie" to 100, "Salad" to -2, "No food" to 0)

    // A class WorldState that holds the row and col indexes as well as the type of state they represent
    data class WorldState(var row: Int, var col: Int, var type: String)

    // A variable to keep track of the agent's current state in terms of position and state of that location
    private var agentState = WorldState(0, 0, world[0][0])

    // Create initial policies based on qLearning and iterative
    private var iterativePolicy: MutableList<String> = MutableList(worldDim*worldDim) { "U" }

    /**
     * Set the state of the particular position in the world to the specified type. Used to create the world.
     *
     * Example: Set position (1,2) to "Salad"
     *
     * @param row: Int representing the index of the row starting from 0 to be updated
     * @param col: Int representing the index of the column starting from 0 to be updated
     * @param type: String representing one of the 4 states: "Cheese", "Cookie", "Salad", or "No food" to set that square to represent
     */
    fun updateState(row: Int, col: Int, type: String) {
        world[row][col] = type
    }

    /**
     * Print out the current QTable by printing the values at each position and action:
     *
     * Example:
     *            U      D      L      R
     *          ------------------------
     * P1 (0,0) | 0      0      2      0
     * P2 (0,1) | 10     0      3      0
     * P3 (1,0) | 3      1      0      2
     */
    fun getQTable() {
        print("State \t")
        for (action in actions) {
            print("$action \t")
        }
        println()
        for (i in qTable.indices) {
            val row = i / worldDim
            val col = i % worldDim
            print("($row, $col)\t")

            for (value in qTable[i]) {
                print("$value \t")
            }
            println()
        }
    }

    /**
     * Change the state of the mouse (position and state of that location) based on the action taken
     */
    fun updateAgentState(action: String) {
        when (action) {
            "U" -> agentState.row--
            "D" -> agentState.row++
            "L"-> agentState.col--
            "R" -> agentState.col++
        }
    }

    /**
     * Update the QTable based on the action taken and the qValue associated with it
     *
     * @param state: A WorldState representing the mouse's current position
     * @param action: A string representing the action the mouse is taking
     * @param qValue: An Int representing the qValue associated with that action
     */
    fun updateQTable(state: WorldState, action: String, qValue: Int) {
        val row = (state.row*worldDim + state.col)
        var col = 0
        when (action) {
            "U" -> col = 0
            "D" -> col = 1
            "L" -> col = 2
            "R" -> col = 3
        }
        qTable[row][col] = qValue
    }

    /**
     * Get the qValue of a certain action based on the current state of the mouse
     *
     * @param state: A WorldState representing the mouse's current position
     * @param action: A string representing the action to look up the qValue for
     *
     * @return An Int representing the qValue associated with the action taken
     */
    fun qLookup(state: WorldState, action: String): Int {
        var col: Int = 0
        when (action) {
            "D" -> col = 1
            "L" -> col = 2
            "R" -> col = 3
        }
        val row = (state.row*worldDim + state.col)
        return qTable[row][col]
    }

    /**
     * Get the reward associated with an action based on the current state of the mouse
     *
     * Create a copy of the current position of the mouse to hold the position and state of the new position once the
     * action is taken. Upon this, the reward is found based on the type of the state at the new location.
     *
     * @param state: A WorldState representing the mouse's current position
     * @param action: A string representing the action to look up the reward for
     *
     * @return Int representing the reward at the new position after given action is taken
     */
    fun computeReward(state: WorldState, action: String): Int {
        val stateCopy = WorldState(state.row, state.col, state.type)
        when (action) {
            "U" -> stateCopy.row--
            "D" -> stateCopy.row++
            "L" -> stateCopy.col--
            "R" -> stateCopy.col++
        }
        val nextStateType: String = world[state.row][state.col]
        return rewards[nextStateType]!!
    }


    /**
     * Update qTable given an action to take and perform that action by updating agentState
     *
     * @param action: A string representing the action to update the qTable for
     */
    fun qFunction(action: String) {
        println("Start state: ${agentState.row}, ${agentState.col}")
        val originalState = agentState.copy()
        val currentQ = qLookup(agentState, action)
        println("Moving agent $action from ${agentState.row}, ${agentState.col}")
        updateAgentState(action)
        val currReward = computeReward(agentState, action)
        val qMaximum = qMax()
        println("Now agent is in ${agentState.row}, ${agentState.col}")
        val newQ = currentQ + alpha * (currReward + gamma * qMaximum - currentQ)
        println("Value at curr state: $newQ")
        updateQTable(originalState, action, newQ.toInt())
    }

    fun qMax(): Int {
        val qList = mutableListOf<Int>()
        for (action in actions) {
            if (!isInvalidAction(agentState, action)) {
                qList.add(qLookup(agentState, action))
            }
        }
        return qList.max()
    }

    /**
     * Identify valid action associated with maximum qValue of a given state
     *
     * @return A string representing the action associated with the maximum qValue
     */
    fun qMaxAction(): String {
        var maxAction = "NULL"
        var maxQVal = -10000
        var qVal = -10000
        for (action in actions) {
            if (!isInvalidAction(agentState, action)) {
                qVal = qLookup(agentState, action)
                if (maxQVal < qVal) {
                    maxQVal = qVal
                    maxAction = action
                }
            }
        }
        return maxAction
    }

    fun isInvalidAction(state: WorldState, action: String): Boolean {
        val stateCopy = WorldState(state.row, state.col, state.type)
        when (action) {
            "U" -> stateCopy.row--
            "D" -> stateCopy.row++
            "L" -> stateCopy.col--
            "R" -> stateCopy.col++
        }

        return (stateCopy.row > 3 || stateCopy.row < 0 || stateCopy.col > 3 || stateCopy.col < 0)
    }

    fun trainQPolicy(nSteps: Int, epsilon: Int) {
        val min = 0
        val max = 100
        // Chose an initial action at random
        var action = actions.random()
        var yetToBeValid = true
        while (yetToBeValid) {
            action = actions.random()
            yetToBeValid = isInvalidAction(agentState, action)
        }
        // Update the q value based on the first action
        qFunction(action)
        // Then, update the policy (implicitly stored in the q function) for n steps using
        // an e-greedy approach (probability e that the action is random versus from the q table
        for (step in 0 until nSteps) {
            var num = Random.nextInt(0, 100)
            yetToBeValid = true
            if (num < epsilon) {
                while (yetToBeValid) {
                    action = actions.random()
                    yetToBeValid = isInvalidAction(agentState, action)
                }
                qFunction(action)
            } else {
                action = qMaxAction()
                qFunction(action)
            }
        }

    }

    fun printWorld() {
        println("World: ")
        val emojiMap = mapOf(
            "Cheese" to "🧀",
            "Cookie" to "🍪",
            "Salad" to "🥗",
            "No food" to "⬜"
        )
        for (row in 0 until worldDim) {
            print("$row | ")
            for (col in 0 until worldDim) {
                val item = world[row][col]
                print("${emojiMap[item] ?: item}   ")
            }
            println()
        }
    }

    fun extractPolicy(): MutableList<MutableList<String>> {
        val policy = MutableList(worldDim) { MutableList(worldDim) { "U" } }
        for (row in 0 until worldDim) {
            for (col in 0 until worldDim) {
                val state = WorldState(row, col, world[row][col])
                var bestAction = "U"
                var bestQValue = Int.MIN_VALUE

                for (action in actions) {
                    val qValue = qLookup(state, action)
                    if (qValue > bestQValue) {
                        bestQValue = qValue
                        bestAction = action
                    }
                }

                policy[row][col] = bestAction
            }
        }
        return policy
    }

    fun printPolicy(policy: MutableList<MutableList<String>>) {
        println("Learned policy:")
        for (row in policy) {
            for (action in row) {
                print("$action\t")
            }
            println()
        }
    }

    /**
     *
     */
    fun policyValueFunction(): MutableList<Double> {
        var valueTable: MutableList<Double> = MutableList(worldDim * worldDim) { 0.0 }

        val threshold = 1e-10

        var changedValue: Boolean
        do {
            changedValue = false
            for (state in 0 until worldDim * worldDim) {
                var newValue = 0.0

                val row = state / worldDim
                val col = state % worldDim
                val worldState = WorldState(row, col, world[row][col])

                for (action in listOf("U", "D", "L", "R")) {
                    if (!isInvalidAction(worldState, action)) {
                        val currReward = computeReward(worldState, action)
                        var newRow = row
                        var newCol = col
                        when (action) {
                            "U" -> newRow--
                            "D" -> newRow++
                            "L" -> newCol--
                            "R" -> newCol++
                        }
                        val newIdx = newRow * worldDim + newCol
                        val potentialValue = currReward + gamma * valueTable[newIdx]

                        newValue = maxOf(newValue, potentialValue)
                    }
                }

                if (abs(valueTable[state] - newValue) > threshold) {
                    valueTable[state] = newValue
                    changedValue = true
                }
            }
        } while (changedValue)

        return valueTable
    }


    /**
     *
     */
    fun getIterativePolicy(valueTable: MutableList<Double>): MutableList<String> {
        val policy = MutableList(worldDim*worldDim) { "U" }
        for (state in 0..worldDim*worldDim - 1) {
            var maxAction = "U"
            var maxValue = 0.0

            val row = state / worldDim
            val col = state % worldDim
            val worldState = WorldState(row, col, world[row][col])

            for (action in listOf("U", "D", "L", "R")) {
                if (!isInvalidAction(worldState, action)) {
                    val currReward = computeReward(worldState, action)
                    var newRow = row
                    var newCol = col
                    when (action) {
                        "U" -> newRow--
                        "D" -> newRow++
                        "L" -> newCol--
                        "R" -> newCol++
                    }
                    val newIdx = newRow * worldDim + newCol
                    val thisValue = alpha * (currReward + gamma * valueTable[newIdx])

                    if (thisValue > maxValue) {
                        maxValue = thisValue
                        maxAction = action
                    }
                }
            }
            policy[state] = maxAction
        }
        return policy
    }

    fun policyIterate() {
        var originalPolicy = MutableList(worldDim*worldDim) { "U" }
        val iterations = 50

        for (i in 0 until 50) {
            val thisValueFunction = policyValueFunction()
            val thisPolicy = getIterativePolicy(thisValueFunction)

            if (thisPolicy == originalPolicy) {
                println("Found convergence at step ${i+1}")
                break
            }
            originalPolicy = thisPolicy
        }
        iterativePolicy = originalPolicy
    }

    fun printIterativePolicy() {
        println("Iterative policy:")
        var prevRow = 0
        for (idx in 0..<worldDim*worldDim) {
            val row = idx / worldDim
            if (row != prevRow) {
                println()
                prevRow = row
            }
            print("${iterativePolicy[idx]}   ")
        }
    }




}

fun main() {
    val mouseAgent = QLearning(4, 4, 0, "No food",1)
    mouseAgent.updateState(0, 1, "Salad")
    mouseAgent.updateState(2, 1, "Cheese")
    mouseAgent.updateState(2, 2, "Cookie")
    mouseAgent.trainQPolicy(1000, 50)
    println(mouseAgent.getQTable())
    mouseAgent.printWorld()
    val p = mouseAgent.extractPolicy()
    mouseAgent.printPolicy(p)
    mouseAgent.policyIterate()
    println(mouseAgent.printIterativePolicy())
}
