package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog.LeaveDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog.NextQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manage dialogs, questions, responses and actions
 */
final public class DialogService implements PreloadableService {
    final private QuestionRepository questionRepository;
    final private ResponseActionRepository responseActionRepository;
    final private ParametersResolver parametersResolver;
    final private Logger logger;

    final private Map<String, ActionFactory> actionFactories = new HashMap<>();
    final private Map<Integer, NpcQuestion> questions = new HashMap<>();
    final private Map<Integer, Response> responses = new HashMap<>();

    public DialogService(QuestionRepository questionRepository, ResponseActionRepository responseActionRepository, ActionFactory[] actionFactories, ParametersResolver parametersResolver, Logger logger) {
        this.questionRepository = questionRepository;
        this.responseActionRepository = responseActionRepository;
        this.parametersResolver = parametersResolver;
        this.logger = logger;

        initActionFactories(actionFactories);
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading dialogs responses...");
        createResponses(responseActionRepository.all());
        logger.info("{} responses loaded", responses.size());

        logger.info("Loading dialogs questions...");
        questionRepository.all().forEach(this::createQuestion);
        logger.info("{} questions loaded", questions.size());
    }

    /**
     * Get list of questions for a given NPC
     */
    public Collection<NpcQuestion> forNpc(Npc npc) {
        return byIds(npc.questions());
    }

    /**
     * Get list of questions
     *
     * Note: If the returned questions size if different from requested questions, a warning will be logged without fail
     */
    public Collection<NpcQuestion> byIds(int[] ids) {
        return loadQuestionFromCache(ids).orElseGet(() -> loadQuestionFromDatabase(ids));
    }

    /**
     * Register a new response action factory
     */
    public void registerActionFactory(ActionFactory factory) {
        actionFactories.put(factory.type(), factory);
    }

    /**
     * Create or retrieve a NpcQuestion from an entity
     */
    private NpcQuestion createQuestion(Question entity) {
        return questions.computeIfAbsent(
            entity.id(),
            id -> new NpcQuestion(
                entity,
                createResponses(responseActionRepository.byQuestion(entity)),
                parametersResolver
            )
        );
    }

    /**
     * Try to load all questions from cache
     * If at least one question is not into the cache, will returns nothing (and let loading from database)
     */
    private Optional<Collection<NpcQuestion>> loadQuestionFromCache(int[] ids) {
        Collection<NpcQuestion> questions = new ArrayList<>(ids.length);

        for (int id : ids) {
            if (!this.questions.containsKey(id)) {
                return Optional.empty();
            }

            questions.add(this.questions.get(id));
        }

        return Optional.of(questions);
    }

    /**
     * Load all questions from database
     */
    private Collection<NpcQuestion> loadQuestionFromDatabase(int[] ids) {
        Collection<NpcQuestion> questions = questionRepository.byIds(ids)
            .stream()
            .map(this::createQuestion)
            .collect(Collectors.toList())
        ;

        if (questions.size() != ids.length) {
            logger.warn(
                "NPC question not found : requested {}, actual {}",
                Arrays.toString(ids),
                questions.stream()
                    .map(question -> Integer.toString(question.id()))
                    .collect(Collectors.joining(", ", "[", "]"))
            );
        }

        return questions;
    }

    /**
     * Create responses from actions
     *
     * @param responsesActions Actions, grouping by the response id
     */
    private Collection<Response> createResponses(Map<Integer, List<ResponseAction>> responsesActions) {
        Collection<Response> responses = new ArrayList<>();

        for (Map.Entry<Integer, List<ResponseAction>> entry : responsesActions.entrySet()) {
            responses.add(
                this.responses.computeIfAbsent(
                    entry.getKey(),
                    id -> new Response(
                        id,
                        entry.getValue().stream()
                            .map(this::createAction)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
                    )
                )
            );
        }

        return responses;
    }

    /**
     * Create a single response action
     * Note: If the action is not supported, the method will log a warning, and return null
     *
     * @return The created action or null if no factory is found (a warning will be logged)
     */
    private Action createAction(ResponseAction action) {
        if (!actionFactories.containsKey(action.action())) {
            logger.warn("Response action {} is not supported for response {}", action.action(), action.responseId());

            return null;
        }

        return actionFactories.get(action.action()).create(action);
    }

    /**
     * Initialize factories
     * Note: Because of circular references, all dialog actions are registered here instead of the module
     */
    private void initActionFactories(ActionFactory[] actionFactories) {
        for (ActionFactory actionFactory : actionFactories) {
            registerActionFactory(actionFactory);
        }

        // Register dialog actions
        registerActionFactory(new NextQuestion.Factory(this));
        registerActionFactory(new LeaveDialog.Factory());
    }
}
