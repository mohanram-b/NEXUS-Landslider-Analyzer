package com.example.data.repository

import com.example.data.local.NexusDatabase
import com.example.data.local.entities.*
import com.example.data.model.*
import java.util.UUID

object NexusDatabaseSeeder {

    suspend fun seedIfEmpty(database: NexusDatabase) {
        if (database.incidentDao().getIncidentCount() > 0) return

        val currentTime = System.currentTimeMillis()
        val oneHour = 3600 * 1000L
        val oneDay = 24 * 3600 * 1000L

        // 1. Seed 5 Users
        val users = listOf(
            UserEntity("usr_admin", "Commander Elena Vance", "elena.vance@nexus.gov", UserRole.ADMIN),
            UserEntity("usr_operator", "Marcus Hayes", "marcus.h@dispatch.nexus.gov", UserRole.OPERATOR),
            UserEntity("usr_water_lead", "Dr. Robert Chen", "r.chen@water.gov", UserRole.DEPARTMENT, "dept_water"),
            UserEntity("usr_field_ops", "Officer Sarah Jenkins", "s.jenkins@police.gov", UserRole.OPERATOR, "dept_police"),
            UserEntity("usr_reporter", "Civic Observer #142", "reporter142@citizen.org", UserRole.REPORTER)
        )
        database.userDao().insertUsers(users)

        // 2. Seed 5 Departments
        val departments = listOf(
            DepartmentEntity("dept_water", "Water Management & Drainage Dept", "WATER", "dispatch@water.gov", "+1 (555) 019-4820", 18, "Hydrology Unit Alpha"),
            DepartmentEntity("dept_transport", "Transportation & Road Infrastructure", "TRANSPORTATION", "ops@roads.gov", "+1 (555) 019-7712", 24, "Pavement Taskforce 3"),
            DepartmentEntity("dept_power", "Power Grid & Electrical Safety Board", "POWER", "grid@power.gov", "+1 (555) 019-3341", 14, "Substation Rapid Crew"),
            DepartmentEntity("dept_police", "Public Safety & Traffic Control", "SAFETY", "command@police.gov", "+1 (555) 019-9110", 35, "Metro Patrol Sector 4"),
            DepartmentEntity("dept_hazard", "Emergency Medical & Hazmat Division", "HAZARD", "hazmat@emergency.gov", "+1 (555) 019-5500", 16, "Hazmat Response Team 1")
        )
        database.departmentDao().insertDepartments(departments)

        // 3. Seed 10 Sensors
        val sensors = listOf(
            SensorEntity("snr_flood_01", "West River Underpass Depth Gauge", SensorType.WATER_LEVEL, SensorStatus.ACTIVE, 37.7749, -122.4194, "Market St & 12th Ave Underpass", 14.5, "cm", 50.0, 35.0, currentTime),
            SensorEntity("snr_flood_02", "South Bay Tidal Surge Monitor", SensorType.WATER_LEVEL, SensorStatus.ACTIVE, 37.7610, -122.3880, "South Bay Drainage Canal #3", 22.0, "cm", 60.0, 45.0, currentTime),
            SensorEntity("snr_rain_01", "Downtown Storm Precipitation Sensor", SensorType.RAIN_GAUGE, SensorStatus.ACTIVE, 37.7858, -122.4065, "Downtown Financial Core", 4.2, "mm/h", 25.0, 15.0, currentTime),
            SensorEntity("snr_grid_01", "Eastside Substation Transformer Load", SensorType.GRID_POWER_LOAD, SensorStatus.ACTIVE, 37.7550, -122.4000, "Substation 9 - Industrial District", 480.0, "kW", 750.0, 600.0, currentTime),
            SensorEntity("snr_grid_02", "North Hill Feeder Grid Surge Sensor", SensorType.GRID_POWER_LOAD, SensorStatus.ACTIVE, 37.7980, -122.4150, "North Hill High-Voltage Junction", 310.0, "kW", 600.0, 450.0, currentTime),
            SensorEntity("snr_vib_01", "Golden Parkway Bridge Vibration Node", SensorType.ROAD_SURFACE_VIBRATION, SensorStatus.ACTIVE, 37.7800, -122.4400, "Golden Parkway Viaduct Span B", 0.08, "m/s²", 0.45, 0.25, currentTime),
            SensorEntity("snr_vib_02", "Harbor Overpass Structural Seismic Node", SensorType.ROAD_SURFACE_VIBRATION, SensorStatus.ACTIVE, 37.7680, -122.3900, "Harbor Express Viaduct", 0.12, "m/s²", 0.50, 0.30, currentTime),
            SensorEntity("snr_traf_01", "Interstate I-80 Junction Radar", SensorType.TRAFFIC_CONGESTION, SensorStatus.ACTIVE, 37.7710, -122.4100, "I-80 & 5th St Interchange", 45.0, "%", 85.0, 70.0, currentTime),
            SensorEntity("snr_traf_02", "Crosstown Tunnel Flow Radar", SensorType.TRAFFIC_CONGESTION, SensorStatus.ACTIVE, 37.7890, -122.4200, "Crosstown Transit Arterial", 62.0, "%", 88.0, 75.0, currentTime),
            SensorEntity("snr_air_01", "Chemical District Environmental Air Sensor", SensorType.AIR_QUALITY, SensorStatus.ACTIVE, 37.7490, -122.3950, "Port Logistics Sector 2", 38.0, "AQI", 120.0, 80.0, currentTime)
        )
        database.sensorDao().insertSensors(sensors)

        // Seed Sensor historical readings
        val readings = mutableListOf<SensorReadingEntity>()
        sensors.forEach { sensor ->
            for (i in 0..10) {
                val variance = (Math.random() - 0.5) * (sensor.threshold * 0.1)
                val valReading = (sensor.currentValue + variance).coerceAtLeast(0.0)
                readings.add(
                    SensorReadingEntity(
                        id = UUID.randomUUID().toString(),
                        sensorId = sensor.id,
                        value = ((valReading * 10).toInt() / 10.0),
                        unit = sensor.unit,
                        isAlertTrigger = false,
                        recordedAt = currentTime - (i * 15 * 60 * 1000L)
                    )
                )
            }
        }
        database.sensorDao().insertReadings(readings)

        // 4. Seed 40 Realistic Incidents with AI Analysis and Activities
        val incidentSpecs = listOf(
            // Critical Flooding
            Triple("Severe Flash Flooding at 12th St Underpass", "Over 65cm of rapid stormwater accumulation trapping multiple vehicles. Stormwater pumps failed.", IncidentCategory.FLOODING),
            Triple("Substation 9 Transformer Arc & Sparking Wire", "Live 4.1kV distribution wire downed across sidewalk after lightning strike. Visible electrical arcing.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Sinkhole Expansion on Grand Avenue Commuter Artery", "Sub-surface roadbed collapse creating 3-meter wide void on eastbound lane. Heavy traffic stalled.", IncidentCategory.ROAD_DAMAGE),
            Triple("Main Storm Drain Culvert Total Blockage", "Debris accumulation causing backflow inundation into 4 adjacent commercial basements.", IncidentCategory.DRAINAGE_FAILURE),
            Triple("Multi-Vehicle Pileup on Wet Expressway Surface", "Four cars involved in rollover collision due to slick hydroplaning conditions. Fuel leak observed.", IncidentCategory.ACCIDENT),
            Triple("Industrial Chemical Spill on Logistics Route", "Hazardous coolant container breached following transport collision. Fumes detected.", IncidentCategory.PUBLIC_SAFETY),

            // High Severity
            Triple("Power Feeder Pole Leaning Precariously", "High-voltage utility pole cracked at base after storm gusts. Dangling over residential driveway.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Riverbank Overflow Threatening Golden Bridge Pier", "Rising river level encroaching on bridge support foundation with high-velocity debris impact.", IncidentCategory.FLOODING),
            Triple("Deep Asphalt Pothole Cluster on Bus Corridor", "Four sharp impact craters causing tire blowouts for morning transit commuters.", IncidentCategory.ROAD_DAMAGE),
            Triple("Sewer Manhole Cover Blown Off by Storm Surge", "Open storm sewer geyser venting muddy water 1 meter into roadway intersection.", IncidentCategory.DRAINAGE_FAILURE),
            Triple("Intersection Traffic Light Grid Blackout", "Primary signal controllers offline at 5-way intersection during rush hour.", IncidentCategory.ACCIDENT),
            Triple("Exposed Cable in Flooded Pedestrian Plaza", "Underground conduit submerged in standing puddle with tingling current reported by pedestrians.", IncidentCategory.ELECTRICAL_HAZARD),

            // Moderate & Routine
            Triple("Clogged Catch Basin Causing Curbside Ponding", "Leaf litter blocking grate. Water spilling over sidewalk onto store entrances.", IncidentCategory.DRAINAGE_FAILURE),
            Triple("Guardrail Crushed Along Highway Offramp", "50ft of metal barrier deformed from earlier sideswipe incident.", IncidentCategory.ROAD_DAMAGE),
            Triple("Minor Retention Pond Spillway Seepage", "Stormwater basin water level at 80% capacity with minor overflow along access trail.", IncidentCategory.FLOODING),
            Triple("Tree Branch Resting on Low-Voltage Cable", "Fallen pine limb pressing down on telecommunications and low-voltage service drop.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Fender Bender Blocking Right Turn Lane", "Two sedans immobilizing traffic lane. No severe injuries, awaiting tow truck.", IncidentCategory.ACCIDENT),
            Triple("Graffiti and Damaged Emergency Callbox", "Public safety callbox broken and handset detached near park entrance.", IncidentCategory.PUBLIC_SAFETY),

            // Additional 22 incidents to reach 40
            Triple("Water Main Fracture Flooding Residential Alley", "Clean water pressurized leak eroding pavement near 4th & Pine.", IncidentCategory.FLOODING),
            Triple("Road Pavement Buckling from Heat Expansion", "Asphalt ridge 15cm high across northbound lane creating ramp hazard.", IncidentCategory.ROAD_DAMAGE),
            Triple("Streetlight Knockout at Unlit Crossing", "Damaged illumination fixture causing dark zone at school pedestrian crossing.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Clogged Retention Grate near Elementary School", "Trash build-up preventing runoff into detention basin.", IncidentCategory.DRAINAGE_FAILURE),
            Triple("Delivery Truck Stalled Across Rail Crossing", "Mechanical failure at grade crossing; emergency warning activated.", IncidentCategory.ACCIDENT),
            Triple("Illegal Hazmat Drum Dumping in Vacant Lot", "Two 55-gallon drums with toxic warning labels abandoned near drainage canal.", IncidentCategory.PUBLIC_SAFETY),
            Triple("Storm Inundation of Harbor Parking Structure", "Lower level submerged under 30cm of tidal brackish water.", IncidentCategory.FLOODING),
            Triple("Asphalt Crumbling on Overpass Expansion Joint", "Joint separation causing jarring vibration for heavy freight vehicles.", IncidentCategory.ROAD_DAMAGE),
            Triple("Flickering Street Utility Box with Smoke", "Low-level smoke emitting from underground junction box.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Sediment Runoff Silting Storm Drain Channel", "Construction site silt fence failure depositing mud into main collector.", IncidentCategory.DRAINAGE_FAILURE),
            Triple("Bicycle-Courier Collision on Transit Mall", "Cyclist and pedestrian collision requiring first aid support.", IncidentCategory.ACCIDENT),
            Triple("Damaged Water Meter Box Causing Trip Hazard", "Cast iron cover missing on high-traffic downtown sidewalk.", IncidentCategory.PUBLIC_SAFETY),
            Triple("Canal Bank Erosion Threatening Bike Path", "Water current undercutting 20 meters of asphalt embankment.", IncidentCategory.FLOODING),
            Triple("Unsecured Construction Plates on Arterial Road", "Steel trench plates sliding under heavy bus braking.", IncidentCategory.ROAD_DAMAGE),
            Triple("Sparking Overhead Trolley Wire", "Transit catenary wire sparking intermittently during wet weather.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Basement Sump Pump Discharge Freezing on Road", "Continuous discharge water creating icy hazard zone on curve.", IncidentCategory.DRAINAGE_FAILURE),
            Triple("Disabled RV Blocking Emergency Evacuation Route", "Abandoned vehicle narrowing mountain access lane to one way.", IncidentCategory.PUBLIC_SAFETY),
            Triple("Overturned Utility Trailer on Bridge Approach", "Lumber debris scattered across 2 lanes during morning commute.", IncidentCategory.ACCIDENT),
            Triple("Localized Ponding on Hospital Ambulance Bay", "Poor drainage causing water barrier at emergency intake driveway.", IncidentCategory.FLOODING),
            Triple("Severe Frost Heave on Northern Expressway", "Pavement heave causing hazardous bumps at 65mph speeds.", IncidentCategory.ROAD_DAMAGE),
            Triple("Blown Street Transformer Fuse Cluster", "Neighborhood power outage affecting 350 residential units.", IncidentCategory.ELECTRICAL_HAZARD),
            Triple("Culvert Inflow Grate Obstructed by Tree Log", "Large fallen timber blocking 80% of culvert cross-section.", IncidentCategory.DRAINAGE_FAILURE)
        )

        val incidents = mutableListOf<IncidentEntity>()
        val analyses = mutableListOf<AIAnalysisEntity>()
        val activities = mutableListOf<ActivityEntity>()

        incidentSpecs.forEachIndexed { index, (title, desc, cat) ->
            val id = "inc_${1000 + index}"
            val ageHours = (index * 1.8).toLong()
            val createdTimestamp = currentTime - (ageHours * oneHour)

            // Distribute statuses realistically
            val status = when (index) {
                0, 1 -> IncidentStatus.ANALYZING
                in 2..8 -> IncidentStatus.TRIAGED
                in 9..18 -> IncidentStatus.ASSIGNED
                in 19..28 -> IncidentStatus.IN_PROGRESS
                else -> IncidentStatus.RESOLVED
            }

            // Severity scoring
            val severityScore = when (index) {
                0 -> 94
                1 -> 88
                2 -> 82
                in 3..8 -> (72..85).random()
                in 9..20 -> (50..70).random()
                else -> (25..48).random()
            }
            val severityLevel = SeverityLevel.fromScore(severityScore)
            val impactScore = ((severityScore * 0.85) + (index % 12)).toInt().coerceIn(20, 98)

            // Coordinates in metro region
            val lat = 37.7500 + ((index % 8) * 0.012) + (Math.random() * 0.005)
            val lng = -122.4300 + ((index % 6) * 0.015) + (Math.random() * 0.005)
            val address = "Metro Sector ${((index % 9) + 1)}, Zone ${('A' + (index % 5))}"

            // Assigned department
            val (deptId, deptName) = when (cat) {
                IncidentCategory.FLOODING, IncidentCategory.DRAINAGE_FAILURE -> "dept_water" to "Water Management & Drainage Dept"
                IncidentCategory.ROAD_DAMAGE -> "dept_transport" to "Transportation & Road Infrastructure"
                IncidentCategory.ELECTRICAL_HAZARD -> "dept_power" to "Power Grid & Electrical Safety Board"
                IncidentCategory.ACCIDENT -> "dept_police" to "Public Safety & Traffic Control"
                IncidentCategory.PUBLIC_SAFETY -> "dept_hazard" to "Emergency Medical & Hazmat Division"
            }

            val incident = IncidentEntity(
                id = id,
                title = title,
                description = desc,
                category = cat,
                status = status,
                severityScore = severityScore,
                severityLevel = severityLevel,
                impactScore = impactScore,
                latitude = lat,
                longitude = lng,
                address = address,
                reporterId = users[index % users.size].id,
                reporterName = users[index % users.size].name,
                departmentId = if (status != IncidentStatus.REPORTED && status != IncidentStatus.ANALYZING) deptId else null,
                departmentName = if (status != IncidentStatus.REPORTED && status != IncidentStatus.ANALYZING) deptName else null,
                isSensorTriggered = index == 0 || index == 1,
                triggeredSensorId = if (index == 0) "snr_flood_01" else if (index == 1) "snr_grid_01" else null,
                createdAt = createdTimestamp,
                updatedAt = createdTimestamp + 15 * 60 * 1000L
            )
            incidents.add(incident)

            // Embedded AI Analysis record
            val analysis = AIAnalysisEntity(
                id = "ai_${id}",
                incidentId = id,
                summary = "Automated AI Analysis: Identified ${cat.displayName} incident at $address. Verified active hazards and computed operational response envelope.",
                category = cat.name,
                categoryConfidence = 0.94 - (index * 0.003).coerceAtLeast(0.0),
                hazardsJson = "[\"Public Transit Obstruction\", \"Infrastructure Strain\", \"Localized Safety Hazard\"]",
                severityScore = severityScore,
                severityExplanation = "Severity $severityScore/100: ${severityLevel.displayName} classification based on environmental telemetry, population density, and critical infrastructure risk.",
                duplicateSimilarityScore = if (index in 5..7) 0.76 else 0.12,
                duplicateIncidentId = if (index in 5..7) "inc_1000" else null,
                duplicateRecommendation = if (index in 5..7) "Potential cluster with master ticket #1000" else "Unique independent incident",
                impactScore = impactScore,
                impactPrediction = "Estimated ${if (severityScore > 75) "severe" else "moderate"} arterial disruption with ~${impactScore * 25} individuals in immediate zone.",
                populationAtRisk = if (severityScore > 75) "High Exposure (~1,500+ residents)" else "Moderate Exposure (< 400 individuals)",
                trafficDisruptionLevel = if (severityScore > 75) "Major Arterial Disruption" else "Localized Street Disruption",
                infrastructureRisk = "Municipal Structural & Asset Integrity Risk Level ${severityLevel.displayName}",
                routingDecision = deptName,
                routingConfidence = 0.96,
                routingReason = "Routed directly to $deptName based on specialized tactical capabilities and jurisdictional mandate.",
                responseStrategy = "Deploy Tier-${if (severityScore > 75) "1 Emergency" else "2 Priority"} dispatch unit. Establish field perimeter, initiate active drainage/clearing protocols, and coordinate diversion routes.",
                priorityChecklistJson = "[\"1. Deploy on-site rapid assessment team\", \"2. Position perimeter warning barricades\", \"3. Clear primary hazard vector\", \"4. Log status resolution\"]",
                overallConfidence = 0.95,
                aiModelUsed = "Gemini 3.5 Flash / NEXUS Orchestrator v2",
                createdAt = createdTimestamp + 5000L
            )
            analyses.add(analysis)

            // Timeline activities
            activities.add(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    incidentId = id,
                    userId = incident.reporterId,
                    userName = incident.reporterName,
                    type = ActivityType.INCIDENT_CREATED,
                    message = "Incident reported: $title",
                    createdAt = createdTimestamp
                )
            )
            activities.add(
                ActivityEntity(
                    id = UUID.randomUUID().toString(),
                    incidentId = id,
                    userId = "nexus_ai",
                    userName = "NEXUS AI Orchestrator",
                    type = ActivityType.AI_ANALYZED,
                    message = "AI Analysis completed: Severity $severityScore/100 (${severityLevel.displayName}), Routed to $deptName",
                    createdAt = createdTimestamp + 5000L
                )
            )
            if (status == IncidentStatus.ASSIGNED || status == IncidentStatus.IN_PROGRESS || status == IncidentStatus.RESOLVED) {
                activities.add(
                    ActivityEntity(
                        id = UUID.randomUUID().toString(),
                        incidentId = id,
                        userId = "usr_operator",
                        userName = "Marcus Hayes (Dispatch)",
                        type = ActivityType.DEPARTMENT_ASSIGNED,
                        message = "Assigned to $deptName for tactical dispatch",
                        createdAt = createdTimestamp + 15 * 60 * 1000L
                    )
                )
            }
            if (status == IncidentStatus.RESOLVED) {
                activities.add(
                    ActivityEntity(
                        id = UUID.randomUUID().toString(),
                        incidentId = id,
                        userId = "usr_operator",
                        userName = "Marcus Hayes (Dispatch)",
                        type = ActivityType.RESOLVED,
                        message = "Incident successfully resolved and closed",
                        createdAt = createdTimestamp + 90 * 60 * 1000L
                    )
                )
            }
        }

        database.incidentDao().insertIncidents(incidents)
        database.aiAnalysisDao().insertAnalyses(analyses)
        database.activityDao().insertActivities(activities)
    }
}
