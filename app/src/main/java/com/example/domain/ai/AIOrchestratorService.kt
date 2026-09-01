package com.example.domain.ai

import com.example.data.model.FullAIIncidentAnalysis
import com.example.domain.ai.agents.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class AIOrchestratorService(
    private val analysisAgent: IncidentAnalysisAgent = IncidentAnalysisAgent(),
    private val severityAgent: SeverityAgent = SeverityAgent(),
    private val duplicateAgent: DuplicateDetectionAgent = DuplicateDetectionAgent(),
    private val impactAgent: ImpactPredictionAgent = ImpactPredictionAgent(),
    private val routingAgent: RoutingAgent = RoutingAgent(),
    private val responseAgent: ResponseStrategyAgent = ResponseStrategyAgent()
) {

    suspend fun executeIncidentAnalysisPipeline(
        context: AgentContext,
        onStageProgress: ((stageName: String, progress: Float) -> Unit)? = null
    ): FullAIIncidentAnalysis = withContext(Dispatchers.Default) {
        // Stage 1: Incident Analysis Agent
        onStageProgress?.invoke("Running Incident Analysis Agent...", 0.15f)
        delay(120) // Realistic asynchronous agent execution feel
        val analysisResult = analysisAgent.analyze(context)

        // Stage 2: Severity Agent
        onStageProgress?.invoke("Evaluating Severity & Risk Assessment...", 0.35f)
        delay(120)
        val severityResult = severityAgent.score(context, analysisResult)

        // Stage 3: Duplicate Detection Agent
        onStageProgress?.invoke("Performing Spatio-Temporal Duplicate Matching...", 0.55f)
        delay(120)
        val duplicateResult = duplicateAgent.detect(context, analysisResult)

        // Stage 4: Impact Prediction Agent
        onStageProgress?.invoke("Predicting Arterial & Infrastructure Impact...", 0.70f)
        delay(120)
        val impactResult = impactAgent.predict(context, analysisResult, severityResult)

        // Stage 5: Routing Agent
        onStageProgress?.invoke("Calculating Optimal Department Routing...", 0.85f)
        delay(120)
        val routingResult = routingAgent.route(context, analysisResult, severityResult)

        // Stage 6: Response Strategy Agent
        onStageProgress?.invoke("Synthesizing SOP Tactical Strategy...", 0.95f)
        delay(120)
        val strategyResult = responseAgent.plan(context, analysisResult, severityResult, routingResult)

        onStageProgress?.invoke("Pipeline Execution Complete", 1.0f)

        // Overall Confidence calculation
        val overallConfidence = ((analysisResult.categoryConfidence * 0.4) + (routingResult.routingConfidence * 0.4) + 0.18).coerceIn(0.75, 0.99)

        FullAIIncidentAnalysis(
            incidentId = context.incident.id,
            analysis = analysisResult,
            severity = severityResult,
            duplicate = duplicateResult,
            impact = impactResult,
            routing = routingResult,
            strategy = strategyResult,
            overallConfidence = overallConfidence,
            modelSignature = "Gemini 3.5 Flash / NEXUS Orchestrator v2"
        )
    }
}
