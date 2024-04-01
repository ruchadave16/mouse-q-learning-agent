# Q-Learning 

A Kotlin-based single agent reinforement learning implementation that uses both a qvalue-based and policy-based algorithm to evaluate cost and reward across an environment. A policy is extracted for each case to decide the "ideal" action the agent should take. 

## Description of the Project
While machine learning tasks such as classification or regression are just one time tasks, sequential decision making processes refer to making decisions over time where each step influences the next. In reinforcement learning, "agents", or the decision makers, are trained to make sequences of decisions that work towards achieving some goal (generally maximixing a function representing a reward). The decisions taken to reach this type of location, "state", are called "actions". A policy is a set of rules that guides the agent to make decisions.

These problems can be solved by value-based or policy-based methods. Value-based methods try to estimate the value of certain states and actions in the environment. In Q-Learning, this is done by learning a Q-Value for each state and action and then a policy is extracted from the Q-Table storing these Q-Values (generally choosing the maximum of the Q-Values tangent to the current state). Policy-based methods directly learn a policy instead of estimating values for each state/action. 

In this project, we create a sample environment that our agent, a mouse, can travel through (U, D, L, R). There are rewards and punishments (cheese and salad) placed along the path, along with the final goal of getting a cookie. We implement both a value based and a policy based method to create policies for the mouse to navigate the grid. The final results are provided in the main function, which shows how both of our methods result in the same "ideal" policy being chosen.

## Try it yourself!
### Installation Instructions

1. Clone the repository.<br>
  - Using SSH: `git clone git@github.com:ruchadave16/mouse-q-learning-agent.git`
  - Using HTML (not recommended): `https://github.com/ruchadave16/mouse-q-learning-agent.git`
2. Navigate to the local repository using your terminal
  ex: `cd mouse-q-learning-agent`
  If this project is correctly compiled, the following file should be present:
  - src/main/kotlin:
    + QLearning.kt
3. Configure the project.<br>
    a) Since this is built using Gradle, make sure this is installed.
    ```
    $ gradle init
    ```
    b) Follow the instructions on the terminal to set up the project properly. You should now have the following outside the `src` directory, inside the `mouse-q-learning-agent` repo:
    - src/main/kotlin:
      + QLearning.kt
    - gradle
      - wrapper
        + gradle-wrapper.jar
        + gradle-wrapper.properties
    + build.gradle
    + settings.gradle
    + gradlew
    + gradlew.bat
4. Run the project<br>
    Use the following command to run the reinforcement learning simulation and get the Q-Table associated with the environment as well as the policies associated with both the q-learning based value function and iterative policy based algorithm outputted onto the terminal.
    ```
    $ ./gradlew run
    ``` 
