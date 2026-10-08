package com.korenabdush.simplecrm

data class DemoVisitor(
    val visitorId: String,
    val accountId: String,
    val visitorData: Map<String, Any>,
    val accountData: Map<String, Any>,
)

object PendoDemoData {
    val visitors = listOf(
        DemoVisitor("jane.cooper@acme.example", "Acme Corp",
            mapOf("name" to "Jane Cooper", "role" to "Sales Manager", "age" to 34, "country" to "USA"),
            mapOf("Tier" to "Enterprise", "size" to "Large", "industry" to "Manufacturing", "plan" to "Annual")),
        DemoVisitor("devon.lane@globex.example", "Globex Inc.",
            mapOf("name" to "Devon Lane", "role" to "Account Executive", "age" to 29, "country" to "USA"),
            mapOf("Tier" to "Mid-Market", "size" to "Medium", "industry" to "Retail", "plan" to "Monthly")),
        DemoVisitor("cameron.wright@initech.example", "Initech",
            mapOf("name" to "Cameron Wright", "role" to "Customer Success", "age" to 41, "country" to "UK"),
            mapOf("Tier" to "Enterprise", "size" to "Large", "industry" to "Finance", "plan" to "Annual")),
        DemoVisitor("amara.okafor@brightpath.example", "BrightPath Media",
            mapOf("name" to "Amara Okafor", "role" to "Sales Rep", "age" to 26, "country" to "Nigeria"),
            mapOf("Tier" to "SMB", "size" to "Small", "industry" to "Media", "plan" to "Free Trial")),
        DemoVisitor("liam.osullivan@vertexlogix.example", "Vertex Logistics",
            mapOf("name" to "Liam O'Sullivan", "role" to "Operations Director", "age" to 47, "country" to "Ireland"),
            mapOf("Tier" to "Enterprise", "size" to "Large", "industry" to "Logistics", "plan" to "Annual")),
        DemoVisitor("haruto.sato@nexawave.example", "NexaWave Systems",
            mapOf("name" to "Haruto Sato", "role" to "Product Manager", "age" to 33, "country" to "Japan"),
            mapOf("Tier" to "Mid-Market", "size" to "Medium", "industry" to "Technology", "plan" to "Monthly")),
        DemoVisitor("sofia.martinez@lumenhealth.example", "Lumen Health",
            mapOf("name" to "Sofia Martinez", "role" to "VP of Sales", "age" to 39, "country" to "Spain"),
            mapOf("Tier" to "Enterprise", "size" to "Large", "industry" to "Healthcare", "plan" to "Annual")),
        DemoVisitor("noah.andersen@fjordbyte.example", "Fjordbyte AS",
            mapOf("name" to "Noah Andersen", "role" to "Sales Rep", "age" to 24, "country" to "Norway"),
            mapOf("Tier" to "SMB", "size" to "Small", "industry" to "Technology", "plan" to "Free Trial")),
        DemoVisitor("pooja.sharma@orbitalfinance.example", "Orbital Finance",
            mapOf("name" to "Pooja Sharma", "role" to "Customer Success Manager", "age" to 39991, "country" to "India"),
            mapOf("Tier" to "Mid-Market", "size" to "Medium", "industry" to "Finance", "plan" to "Annual")),
        DemoVisitor("ethan.chen@summitgear.example", "Summit Gear Co.",
            mapOf("name" to "Ethan Chen", "role" to "Account Executive", "age" to 28, "country" to "Canada"),
            mapOf("Tier" to "SMB", "size" to "Small", "industry" to "Retail", "plan" to "Monthly")),
    )

    fun randomVisitor(): DemoVisitor = visitors.random()
}
