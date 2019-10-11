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
    ACTIVATEDEVICE,
    ACTIVATEUIANDSPAT,                  // for UI and SPaT
    CANCELLEDREQUEST,
    INITIATESHUTDOWN,
    ACTIVATEUISTANDBY,                  // UI Events
    SPATREADYFORUIDISPLAY,
    DATARECEIVEDFROMSPAT,
    SPATSIGNALEDINTERSECTIONCOMPLETE,
    ACTIVATESPAT,                       // SPaT Events
    INRANGE,
    COLLECTDATA,
    CALCULATEMATH,
    ACTIVATEUIDISPLAY,
    WHILESAFE,
    NOTSAFE,
    THROUGHINTERSECTION,
    OUTOFRANGE,
    SIGNALUISTANDBY
}

enum States{
    INITIALSTATE,
    DEVICEACTIVATED,       // for UI and SPaT
    DEVICEDEACTIVATED,
    FORK,
    TASKS,
    JOIN,
    UIATIVATED,             // UI States
    UISTANDBY,
    UIDISPLAYWAITING,
    ADVISORYDISPLAYED,
    SPATACTIVATED,
    UIDEACTIVATED,
    SPATSTANDBY,            // SPaT States
    TRIGGERADVDISPLAY,
    TRIGGERADVCYCLE,
    INTERSECTINOIDENTIFIED,
    INTERSECTIONCOMPLETE,
    DATACOLLECTED,
    CALCULATIONSCOMPLETE,
    UIDISPLAYREADY,
    ADVISORYREADY,
    DISPLAYSPEEDRANGE,
    DISPLAYSTOP,
    SPATPREPFORSTANDBY,
    SPATDEACTIVATED
}

@Log
@Component
class Runner implements ApplicationRunner{
    
    private final StateMachineFactory<States, Events> factory;
    
    Runner(StateMachineFactory<States, Events> factory)   // ignore this error - IDE flagging - runs
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
        machine.sendEvent(Events.ACTIVATEDEVICE);
        log.info("current state: " + machine.getState().getId().name());
        /*System.out.println("Waiting 5 seconds...");
        Thread.sleep(5000);*/                               // delays thread for 5 seconds
    
        machine.sendEvent(Events.ACTIVATEUIANDSPAT);
        log.info("current state: " + machine.getState().getId().name());
    
        machine.sendEvent(Events.ACTIVATEUISTANDBY);
        log.info("current state: " + machine.getState().getId().name());
        
        machine.sendEvent(Events.SPATREADYFORUIDISPLAY);
        log.info("current state: " + machine.getState().getId().name());
    
        machine.sendEvent(Events.ACTIVATESPAT);
        log.info("current state: " + machine.getState().getId().name());
    
        machine.sendEvent(Events.INRANGE);
        log.info("current state: " + machine.getState().getId().name());
        
        /*Message<Events> eventsMessage = MessageBuilder.withPayload(Events.SPATSIGNALEDINTERSECTIONCOMPLETE).setHeader("a", "b").build();
        machine.sendEvent(eventsMessage);
        log.info("current state: " + machine.getState().getId().name());*/
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
                // initial state to act as buffer since cannot fork on initial state
                .withExternal().source(States.INITIALSTATE).target(States.DEVICEACTIVATED).event(Events.ACTIVATEDEVICE)
                .and()
                
                // UI transitions
                .withExternal().source(States.DEVICEACTIVATED).target(States.UIATIVATED).event(Events.ACTIVATEUIANDSPAT)
                .and()
                .withExternal().source(States.UIATIVATED).target(States.UISTANDBY).event(Events.ACTIVATEUISTANDBY)
                .and()
                .withExternal().source(States.UISTANDBY).target(States.UIDISPLAYWAITING).event(Events.SPATREADYFORUIDISPLAY)
                .and()
                .withExternal().source(States.UIDISPLAYWAITING).target(States.ADVISORYDISPLAYED).event(Events.DATARECEIVEDFROMSPAT)
                
                // SPaT transitions
                /*.and()
                .withExternal().source(States.DEVICEACTIVATED).target(States.SPATACTIVATED).event(Events.ACTIVATEUIANDSPAT)
                .and()
                .withExternal().source(States.SPATACTIVATED).target(States.SPATSTANDBY).event(Events.ACTIVATESPAT)
                .and()
                .withExternal().source(States.SPATSTANDBY).target(States.TRIGGERADVCYCLE).event(Events.INRANGE)*/;
                
         /*     .withExternal().source(States.READY).target(States.FORK).event(Events.ACTIVATE)
                .and()
                .withFork().source(States.FORK).target(States.TASKS)
                .and()
                .withExternal().source(States.UIATIVATED).target(States.UISTANDBY)
                .and()
                .withExternal().source(States.SPATACTIVATED).target(States.SPATSTANDBY);
         */
    }
    
    // configure the states involved in the machine
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception
    {
        states
                .withStates().initial(States.INITIALSTATE)  // initial state
                .state(States.DEVICEACTIVATED)
                .state(States.UIATIVATED)
                .state(States.SPATACTIVATED)
                .state(States.UISTANDBY)
                .state(States.UIDISPLAYWAITING)
                .state(States.ADVISORYDISPLAYED)
                .state(States.UISTANDBY)
                .state(States.UIDEACTIVATED)
                .state(States.SPATSTANDBY)
                .state(States.TRIGGERADVCYCLE)
                .end(States.DEVICEDEACTIVATED);  // final state
        
        
        /*
        
          */
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

    
