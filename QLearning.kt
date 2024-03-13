import kotlin.random.Random

class QLearning(actionSpace: Int, private var worldDim: Int, defaultValue: Int, defaultState: String, private var alpha: Int) {
    private val qTable: MutableList<MutableList<Int>> = MutableList(worldDim*worldDim) { MutableList(actionSpace) { defaultValue } }
    private val actions = setOf("U","D","L","R")
    private var gamma = 0.5
    private val world = MutableList(worldDim) { MutableList(worldDim) { defaultState } }
    private val rewards = mutableMapOf("Cheese" to 2, "Cookie" to 100, "Salad" to -2, "No food" to 0)
    data class WorldState(var row: Int, var col: Int, var type: String)
    private var agentState = WorldState(0, 0, world[0][0])


    fun updateState(row: Int, col: Int, type: String) {
        world[row][col] = type
    }

    fun getQTable(){
        for (row in qTable) {
            for (value in row) {
                print("$value ")
            }
            println()
        }
    }

    fun updateAgentState(action: String) {
        when (action) {
            "U" -> agentState.row--
            "D" -> agentState.row++
            "L"-> agentState.col--
            "R" -> agentState.col++
        }
    }

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

    // For q learning
    //Calculates q value, updates table
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

    fun qMax(): Int {
        val qList = mutableListOf<Int>()
        for (action in actions) {
            if (!isInvalidAction(agentState, action)) {
                qList.add(qLookup(agentState, action))
            }
        }
        return qList.max()
    }

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

    // For policy iteration
    fun qFunctionPolicyIteration(state: String, action: String) {}

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
}

fun main() {
    val mouseAgent = QLearning(4, 4, 0, "No food",1)
    mouseAgent.updateState(0, 1, "Salad")
    mouseAgent.updateState(2, 1, "Cheese")
    mouseAgent.updateState(2, 2, "Cookie")
    mouseAgent.trainQPolicy(1000, 50)
    println(mouseAgent.getQTable())

}
