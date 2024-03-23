# Q-Learning 

A Kotlin-based single agent reinforement learning implementation that uses both a qvalue-based and policy-based algorithm to evaluate cost and reward across an environment. A policy is extracted for each case to decide the "ideal" action the agent should take. 

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
