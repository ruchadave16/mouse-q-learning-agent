package org.example

import kotlin.random.Random

/**
 *
 */
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
    fun getQTable(){
        for (row in qTable) {
            for (value in row) {
                print("$value ")
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
    fun updateQTable(state_: WorldState, action: String, qValue: Int) {
        val state = agentState
        val row = (state.row*worldDim + state.col)
        var col = 0
        when (action) {
//            "U" -> col = 0
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
    fun qLookup(state_: WorldState, action: String): Int {
        val state = agentState
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
    fun computeReward(state_: WorldState, action: String): Int {
        val state = agentState
        val stateCopy = WorldState(state.row, state.col, state.type)
        when (action) {
            "U" -> stateCopy.row--
            "D" -> stateCopy.row++
            "L" -> stateCopy.col--
            "R" -> stateCopy.col++
        }
        val nextStateType: String = world[stateCopy.row][stateCopy.col]
        return rewards[nextStateType]!!
    }

    /**
     * Check if the action being performed is invalid (leads to a position off the grid)
     *
     * @param state: A WorldState representing the mouse's current position
     * @param action: A string representing the action to look up the reward for
     *
     * @return A boolean representing whether the position of the agent after the action is taken is valid or not
     */
    fun isInvalidAction(state_: WorldState, action: String): Boolean {
        val state = agentState
        val stateCopy = WorldState(state.row, state.col, state.type)
        when (action) {
            "U" -> stateCopy.row--
            "D" -> stateCopy.row++
            "L" -> stateCopy.col--
            "R" -> stateCopy.col++
        }

        return (stateCopy.row >= worldDim || stateCopy.row < 0 || stateCopy.col >= worldDim || stateCopy.col < 0)
    }

    /**
     * Find the maximum qValue out of any valid action the mouse can take
     *
     * @return Int representing the qValue of the best valid action the mouse can take
     */
    fun qMax(): Int {
        val qList = mutableListOf<Int>()
        for (action in actions) {
            if (!isInvalidAction(agentState, action)) {
                qList.add(qLookup(agentState, action))
            }
        }
        return qList.max()!!
    }

    /**
     * Update qTable given an action to take and perform that action by updating agentState
     *
     * @param action: A string representing the action to look update the qTable for
     */
    fun qFunction(action: String) {
        println("Start state: ${agentState.row}, ${agentState.col}")
        val currentQ = qLookup(agentState, action)
        val qMaximum = qMax()
        val currReward = computeReward(agentState, action)
        println("Moving agent $action from ${agentState.row}, ${agentState.col}")
        updateAgentState(action)
        val newQ = currentQ + alpha * (currReward + gamma * qMaximum - currentQ)
        updateQTable(agentState, action, newQ.toInt())
    }

    /**
     * Identify valid action associated with maximum qValue of a given state
     */
    fun qMaxAction(): String {
        val qList = mutableListOf<Int>()
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

    /**
     *
     */
    fun trainQPolicy(nSteps: Int, epsilon: Int) {
        val min = 0
        val max = 100

        // Chose an initial valid action at random
        var action = actions.random()
        var yetToBeValid = isInvalidAction(agentState, action)
        while (yetToBeValid) {
            action = actions.random()
            yetToBeValid = isInvalidAction(agentState, action)
        }

        // Update the q value based on the first action
        qFunction(action)

        // Then, update the policy (implicitly stored in the q function) for n steps using
        // an e-greedy approach (probability e that the action is random versus from the q table
        for (step in 0 until nSteps) {
            var num = Random.nextInt(min, max)
            yetToBeValid = true
            if (num < epsilon) {
                while (yetToBeValid) {
                    action = actions.random()
                    yetToBeValid = isInvalidAction(agentState, action)
                }
            } else {
                action = qMaxAction()
            }
            qFunction(action)
        }
    }

    /**
     * Calculate policy for QLearning
     *
     * @return A table with probabilities of picking each action at each state based on the qLearning values
     */
    fun qLearningPolicy() {
        val thisPolicy: MutableList<MutableList<Int>> = MutableList(worldDim*worldDim) { MutableList(actionSpace) { 0 } }
        for (state in thisPolicy) {
            val thisSum = sum(qTable[state])
            thisPolicy[state][0] = qTable[state][0] / thisSum
            thisPolicy[state][1] = qTable[state][1] / thisSum
            thisPolicy[state][2] = qTable[state][2] / thisSum
            thisPolicy[state][3] = qTable[state][3] / thisSum
        }
        return thisPolicy
    }

    /**
     *
     */
    fun policyValueTable(policy: MutableList<String>) {
        // Initialize value table for each position to 0
        var valueTable: MutableList<MutableList<Int>> = MutableList(worldDim*worldDim) { MutableList(actionSpace) { 0 } }

        // Update each value in table based on policy
        for (row in valueTable) {
            val value = 0
            for (action in row) {
                value = value + (policy[row] * (currReward + gamma * valueTable[]))
                )) // Get recommended policy for the state
                valueTable[state] = // Implement
            }
        }
    }

    fun policyExtract() {
        // Start with policy to randomly go to any position
        var policy: MutableList<MutableList<Int>> = MutableList(worldDim*worldDim) { MutableList(actionSpace) { 0.25 } }

        for (state in policy) {

        }
    }

    /**
     *
     */
    fun policyEvaluation() {
        // Initial police: right always

    }

    // For policy iteration
    /**
     *
     */
    fun qFunctionPolicyIteration(action: String) {}
}

fun main() {
    val mouseAgent = QLearning(4, 4, 0, "No food",1)
    mouseAgent.updateState(0, 1, "Salad")
    mouseAgent.updateState(2, 1, "Cheese")
    mouseAgent.updateState(2, 2, "Cookie")
    mouseAgent.trainQPolicy(1000, 50)
    println(mouseAgent.getQTable())
}
