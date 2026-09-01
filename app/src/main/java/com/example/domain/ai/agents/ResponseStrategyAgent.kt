package com.example.domain.ai.agents

import com.example.data.model.IncidentAnalysisResult
import com.example.data.model.IncidentCategory
import com.example.data.model.ResponseStrategyResult
import com.example.data.model.RoutingResult
import com.example.data.model.SeverityLevel
import com.example.data.model.SeverityResult
import com.example.domain.ai.AgentContext

class ResponseStrategyAgent {
    fun plan(
        context: AgentContext,
        analysis: IncidentAnalysisResult,
        severity: SeverityResult,
        routing: RoutingResult
    ): ResponseStrategyResult {
        val checklist = mutableListOf<String>()
        val strategy: String
        val resources: String
        val safety: String

        when (analysis.category) {
            IncidentCategory.FLOODING, IncidentCategory.DRAINAGE_FAILURE -> {
                strategy = "Tactical Hydrological Response Protocol: Deploy rapid flood abatement units, clear debris at primary drainage gates, and activate stormwater diversion."
                checklist.add("1. Deploy High-Capacity Submersible Pump Unit 4")
                checklist.add("2. Place perimeter water barriers & high-visibility detour signage")
                checklist.add("3. Inspect downstream culvert for physical blockages")
                checklist.add("4. Continuous telemetry monitoring via nearby depth sensor")
                resources = "2x Flood Mitigation Trucks, 4x Submersible Pumps, 2x Drainage Engineers"
                safety = "Ensure field personnel wear insulated high-traction wading gear and personal flotation devices."
            }
            IncidentCategory.ELECTRICAL_HAZARD -> {
                strategy = "Emergency Grid Isolation & Hazard Containment Protocol: Isolate feeder line, verify de-energization, and establish a 50m exclusion perimeter."
                checklist.add("1. Remotely isolate local feeder circuit from SCADA grid control")
                checklist.add("2. Deploy utility line crew with Class 4 dielectric equipment")
                checklist.add("3. Secure 50-meter perimeter and divert civilian traffic")
                checklist.add("4. Inspect transformer housing for overheating and spark hazard")
                resources = "1x High-Voltage Bucket Truck, 1x Grid Diagnostic Unit, 2x Certified Linemen"
                safety = "STRICT SAFETY WARNING: Treat all downed wires as live until physically grounded."
            }
            IncidentCategory.ROAD_DAMAGE -> {
                strategy = "Civil Infrastructure Stabilization & Repair Protocol: Secure structural perimeter, install safety flashers, and deploy cold/hot mix asphalt rapid patch team."
                checklist.add("1. Position advance hazard warning markers 150m upstream")
                checklist.add("2. Core test sub-base stability to verify void expansion")
                checklist.add("3. Apply high-compaction aggregate patch compound")
                checklist.add("4. Coordinate with transit police for partial lane reopening")
                resources = "1x Asphalt Patching Vehicle, 1x Road Roller, 3x Civil Maintenance Crew"
                safety = "Enforce Class 3 reflective gear and illuminated directional arrows on all work vehicles."
            }
            IncidentCategory.ACCIDENT -> {
                strategy = "Multi-Agency Traffic Incident Management (TIM) Protocol: Rapid lane clearance, hazard buffer establishment, and casualty assessment."
                checklist.add("1. Establish upstream safety taper with reflective cones")
                checklist.add("2. Expedite vehicle recovery towing to shoulder clearance zone")
                checklist.add("3. Clear fluid spills using granular absorbent materials")
                checklist.add("4. Broadcast real-time traffic diversion alert")
                resources = "1x Heavy Towing Unit, 1x Incident Response Patrol, Spill Containment Kit"
                safety = "Maintain lookout spotter for oncoming secondary traffic at high-speed approach angles."
            }
            IncidentCategory.PUBLIC_SAFETY -> {
                strategy = "Public Safety Coordination & Rapid Response Protocol: Deploy sector patrol, secure area perimeter, and coordinate civilian safety guidance."
                checklist.add("1. Deploy first-response sector unit for immediate on-scene assessment")
                checklist.add("2. Establish safety perimeter and public access restrictions")
                checklist.add("3. Interface with municipal operations center for tactical support")
                checklist.add("4. Log continuous timeline updates in NEXUS incident log")
                resources = "2x Sector Response Units, Public Notification PA, Mobile Command Terminal"
                safety = "Adhere to standard public safety operational safety protocols."
            }
        }

        return ResponseStrategyResult(
            tacticalStrategy = strategy,
            priorityChecklist = checklist,
            resourceRequirements = resources,
            safetyProtocol = safety
        )
    }
}
