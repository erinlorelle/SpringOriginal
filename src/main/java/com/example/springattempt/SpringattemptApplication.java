package com.example.springattempt;

import lombok.extern.java.Log;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;

import java.sql.Time;       // added to play with timers
import java.time.*;
import java.util.Calendar;
import java.util.Date;
@SpringBootApplication
public class SpringattemptApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(SpringattemptApplication.class, args);    // running the machine
        
        System.out.println("Hello World");  // displaying output to test machine
    }

}

enum Events {
    ACTIVATE_DEVICE,                    // for UI and SPaT
    ACTIVATE_UI_AND_SPAT,
    CANCELLED_REQUEST,
    INITIATE_SHUTDOWN,
    ACTIVATE_UI_STANDBY,                  // UI Events
    SPAT_READY_FOR_UI_DISPLAY,
    DATA_RECEIVED_FROM_SPAT,
    SPAT_SIGNALED_INTERSECTION_COMPLETE,
    ACTIVATE_SPAT_STANDBY,                // SPaT Events
    IN_RANGE,
    COLLECT_DATA,
    CALCULATE_MATH,
    ACTIVATE_UI_DISPLAY,
    WHILE_SAFE,
    NOT_SAFE,
    THROUGH_INTERSECTION,
    OUT_OF_RANGE,
    SIGNAL_UI_STANDBY
}

enum States{
    INITIAL_STATE,          // for UI and SPaT
    DEVICE_ACTIVATED,
    DEVICE_DEACTIVATED,
    FORK,
    TASKS,
    JOIN,
    UI_ACTIVATED,            // UI States
    UI_STANDBY,
    UI_DISPLAY_WAITING,
    ADVISORY_DISPLAYED,
    SPAT_ACTIVATED,
    UI_DEACTIVATED,
    SPAT_STANDBY,            // SPaT States
    TRIGGER_ADV_DISPLAY,
    TRIGGER_ADV_CYCLE,
    INTERSECTION_IDENTIFIED,
    INTERSECTION_COMPLETE,
    DATA_COLLECTED,
    CALCULATIONS_COMPLETE,
    UI_DISPLAY_READY,
    ADVISORY_READY,
    DISPLAY_SPEED_RANGE,
    DISPLAY_STOP,
    SPAT_PREP_FOR_STANDBY,
    SPAT_DEACTIVATED
}

@Log
@Component
class Runner implements ApplicationRunner{
    
    private final StateMachineFactory<States, Events> factory;
    
    Runner(StateMachineFactory<States, Events> factory)   // ignore this error - IDE flagging - still runs
    {
        this.factory = factory;
    }

    // Create and run a state machine
    @Override
    public void run(ApplicationArguments args) throws Exception
    {
        // creates a machine
        StateMachine<States, Events> machine = this.factory.getStateMachine("13232");   // this is made up and not a real machine id
        machine.start();   // starts a machine
        
        // manually activating events to trigger states in the machine
        log.info("current state: " + machine.getState().getId().name());
        machine.sendEvent(Events.ACTIVATE_DEVICE);
        log.info("current state: " + machine.getState().getId().name());
        /*System.out.println("Waiting 5 seconds...");
        Thread.sleep(5000);*/                               // delays thread for 5 seconds
    
        machine.sendEvent(Events.ACTIVATE_UI_AND_SPAT);     
        log.info("current state: " + machine.getState().getId().name());
        
    }
}

@Log
@Configuration
@EnableStateMachineFactory
class SimpleEnumStatemachineConfiguration extends StateMachineConfigurerAdapter<States, Events>
{
    
    // setup the transitions and the states events involved
    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception
    {
        // assigns events to initial and targeted states, and their order
        transitions
                
                // initial state to act as buffer since cannot fork initial state
                .withExternal().source(States.INITIAL_STATE).target(States.DEVICE_ACTIVATED).event(Events.ACTIVATE_DEVICE)
                .and()
                
                // attempt at fork
                /*.withExternal().source(States.DEVICE_ACTIVATED).target(States.FORK).event(Events.ACTIVATE_UI_AND_SPAT)
                .and()
                .withFork().source(States.FORK).target(States.TASKS)
                .and()
                .withExternal().source(States.UI_ACTIVATED).target(States.UI_STANDBY).event(Events.ACTIVATE_UI_STANDBY)
                .and()
                .withExternal().source(States.SPAT_ACTIVATED).target(States.SPAT_STANDBY).event(Events.ACTIVATE_SPAT_STANDBY)
                .and()
                .withJoin().source(States.TASKS).target(States.JOIN)
                .and()
                .withExternal().source(States.JOIN).target(States.DEVICE_DEACTIVATED);*/
    
                // another forkin' attempt
                .withExternal().source(States.DEVICE_ACTIVATED).target(States.FORK).event(Events.ACTIVATE_UI_AND_SPAT)
                .and()
                .withFork().source(States.FORK).target(States.TASKS)
                .and()
                .withExternal().source(States.UI_ACTIVATED).target(States.UI_STANDBY).event(Events.ACTIVATE_UI_STANDBY)
                .and()
                .withExternal().source(States.SPAT_ACTIVATED).target(States.SPAT_STANDBY).event(Events.ACTIVATE_SPAT_STANDBY)
                .and()
                .withJoin().source(States.TASKS).target(States.JOIN)
                .and()
                .withExternal().source(States.JOIN).target(States.DEVICE_DEACTIVATED);
                
    }
    
    // configure the states involved in the machine
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception
    {
        states
                // without fork
                /*.withStates().initial(States.INITIAL_STATE)  // initial state
                .state(States.DEVICE_ACTIVATED)
                .state(States.UI_ACTIVATED)
                .state(States.SPAT_ACTIVATED)
                .state(States.UI_STANDBY)
                .state(States.UI_DISPLAY_WAITING)
                .state(States.ADVISORY_DISPLAYED)
                .state(States.UI_STANDBY)
                .state(States.UI_DEACTIVATED)
                .state(States.SPAT_STANDBY)
                .state(States.TRIGGER_ADV_CYCLE)
                .end(States.DEVICE_DEACTIVATED);*/  // final state
                
                // with fork
                /*.withStates().initial(States.INITIAL_STATE)  // initial state
                .state(States.DEVICE_ACTIVATED).fork(States.FORK).state(States.TASKS).join(States.JOIN)
                .and()
                .withStates().parent(States.TASKS).initial(States.UI_ACTIVATED).end(States.UI_STANDBY)
                .and()
                .withStates().parent(States.TASKS).initial(States.SPAT_ACTIVATED).end(States.SPAT_STANDBY)*/
        
                // another forkin' attempt
                .withStates().initial(States.INITIAL_STATE)  // initial state
                .state(States.DEVICE_ACTIVATED).fork(States.FORK).state(States.TASKS).join(States.JOIN)
                .and()
                .withStates().parent(States.TASKS).initial(States.UI_ACTIVATED).end(States.UI_STANDBY)
                .and()
                .withStates().parent(States.TASKS).initial(States.SPAT_ACTIVATED).end(States.SPAT_STANDBY)
                
                
                .state(States.UI_ACTIVATED)
                .stateEntry(States.UI_ACTIVATED, new Action<States, Events>()
                {
                    @Override
                    public void execute(StateContext<States, Events> context)
                    {
                        
                        System.out.print("\nUI Activated Started\n");
                        for(int i=100;i<=149; i++)
                            System.out.print(i + " ");
                        System.out.print("\nUI Activated Done\n");
                    }
                })
                .state(States.SPAT_ACTIVATED)
                .stateEntry(States.SPAT_ACTIVATED, new Action<States, Events>()
                {
                    @Override
                    public void execute(StateContext<States, Events> context)
                    {
                        System.out.print("\nSPaT Activated Started\n");
                        for(int i=200;i<=249; i++)
                            System.out.print(i + " ");
                        System.out.print("\nSPaT Activated Done\n");
                    }
                })
                .state(States.UI_STANDBY)
                .stateEntry(States.UI_STANDBY, new Action<States, Events>()
                {
                    @Override
                    public void execute(StateContext<States, Events> context)
                    {
                        System.out.print("\nUI Standby Started\n");
                        for(int i=300;i<=349; i++)
                            System.out.print(i + " ");
                        System.out.print("\nUI Standby Done\n");
                    }
                })
                .state(States.UI_DISPLAY_WAITING)
                .state(States.ADVISORY_DISPLAYED)
                .state(States.UI_STANDBY)
                .state(States.UI_DEACTIVATED)
                .state(States.SPAT_STANDBY)
                .stateEntry(States.SPAT_STANDBY, new Action<States, Events>()
                {
                    @Override
                    public void execute(StateContext<States, Events> context)
                    {
                        System.out.print("\nSPaT Standby Started\n");
                        for(int i=400;i<=449; i++)
                            System.out.print(i + " ");
                        System.out.print("\nSPaT Standby Done\n");
                    }
                })
                .state(States.TRIGGER_ADV_CYCLE)
                .end(States.DEVICE_DEACTIVATED);  // final state
        
        
        
        
          
    }
    
    // the "engine" behind the machine
    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception
    {
        StateMachineListenerAdapter<States, Events> adapter = new StateMachineListenerAdapter<States, Events>()
        {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to)
            {
                log.info(String.format("stateChanged(from: %s, to: %s)", from + "", to + ""));
                            }
        };
        config.withConfiguration().autoStartup(false).listener(adapter);
    }
}


