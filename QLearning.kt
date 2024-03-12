import kotlin.random.Random

class QLearning(actionSpace: Int, private var worldDim: Int, defaultValue: Int, defaultState: String, private var alpha: Int) {
    private val qTable: MutableList<MutableList<Int>> = MutableList(worldDim*2) { MutableList(actionSpace) { defaultValue } }
    private val actions = setOf("U","D","L","R")
    private var gamma = 0.5
    private val world = MutableList(worldDim) { MutableList(worldDim) { defaultState } }
    private val rewards = mutableMapOf("Cheese" to 2, "Cookie" to 100, "Salad" to -2, "No food" to 0)
    data class WorldState(var x: Int, var y: Int, var type: String)
    private var agentState = WorldState(0, 0, world[0][0])


    fun updateState(row: Int, col: Int, type: String) {
        world[row][col] = type
    }

    fun getQTable(){
        for (row in qTable) {
            for (value in row) {
                print("$value")
            }
        }
    }

    fun updateAgentState(action: String) {
        when (action) {
            "U" -> agentState.y++
            "D" -> agentState.y--
            "L"-> agentState.x--
            "R" -> agentState.x++
        }
    }

    fun updateQTable(row:Int, col:Int, qValue: Int) {
        qTable[row - 1][col - 1] = qValue
    }

    fun qLookup(state: WorldState, action: String): Int {
        var col: Int = 0
        when (action) {
            "D" -> col = 1
            "L"-> col = 2
            "R" -> col = 3
        }
        val row = (state.x + worldDim + state.y) - 1
        return qTable[row][col]
    }

    fun valueFunction(state: String): Int {
        val rewardAtState = rewards[state]
        //what is the next state? 
        val v: Int = 0
        return v
    }

    fun computeReward(state: WorldState, action: String): Int {
        when (action) {
            "U" -> state.y++
            "D" -> state.y--
            "L" -> state.x--
            "R" -> state.x++
        }
        val nextStateType: String = world[state.x][state.y]
        return rewards[nextStateType]!!
    }

    // For q learning
    //Calculates q value, updates table
    fun qFunction(action: String) {
        val currentQ = qLookup(agentState, action)
        val qMaximum = qMax()
        val currReward = computeReward(agentState, action)
        updateAgentState(action)
        val newQ = currentQ + alpha * (currReward + gamma * qMaximum - currentQ)
        updateQTable(agentState.x, agentState.y, newQ.toInt())
    }

    fun qMax(): Int {
        val qList = mutableListOf<Int>()
        for (action in actions) {
            qList.add(qLookup(agentState, action))
        }
        return qList.max()
    }

    fun qMaxAction(): String {
        val qList = mutableListOf<Int>()
        var maxAction = "NULL"
        var maxQVal = -10000
        var qVal = -10000
        for (action in actions) {
            qVal = qLookup(agentState, action)
            if (maxQVal < qVal) {
                maxQVal = qVal
                maxAction = action
            }
        }
        return maxAction
    }

    // For policy iteration
    fun qFunctionPolicyIteration(state: String, action: String) {}

    fun trainQPolicy(nSteps: Int, epsilon: Int) {
        val min = 0
        val max = 100
        // Chose an initial action at random
        val firstAction = actions.random()
        var action = actions.random()
        // Update the q value based on the first action
        qFunction(firstAction)
        // Then, update the policy (implicitly stored in the q function) for n steps using
        // an e-greedy approach (probability e that the action is random versus from the q table
        for (step in 0 until nSteps) {
            var num = Random.nextInt(0, 100)
            if (num < epsilon) {
                action = actions.random()
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
    mouseAgent.updateState(0, 0, "Cookie")
    mouseAgent.updateState(0, 1, "Salad")
    mouseAgent.updateState(2, 1, "Cheese")
    mouseAgent.updateState(2, 2, "Cheese")
    mouseAgent.trainQPolicy(100, 50)
    println(mouseAgent.getQTable())

}
