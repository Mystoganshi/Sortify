package com.example.sortify.model

enum class WasteCategory {
    PAPER, PLASTIC, METAL, GLASS, ORGANIC, EWASTE, OTHER
}

data class WasteInfo(
    val classId: Int,
    val name: String,
    val recyclable: Boolean,
    val category: WasteCategory,
    val whatItIs: String,
    val why: String,
    val howToDispose: String,
    val doTips: List<String>,
    val dontTips: List<String>,
    val commonConfusions: String
)

object WasteCatalog {

    // labels.txt order exactly (UPDATED)
    // 0..29
    val NAMES = listOf(
        "Aerosol Cans",                 // 0
        "Ballpen",                      // 1
        "Battery",                      // 2
        "Bottle Cap",                   // 3
        "Cardboard",                    // 4
        "Clothes",                      // 5
        "Disposable Plastic Cutlery",   // 6
        "Eggshells",                    // 7
        "Footwear",                     // 8
        "Garbage Bag",                  // 9
        "Glass Bottles",                // 10
        "HDPE",                         // 11  (NEW, replaced Tea Bags in your dataset)
        "Keyboard",                     // 12
        "Light Bulb",                   // 13
        "Magazines",                    // 14
        "Mobile",                       // 15
        "Monitor",                      // 16
        "Mouse",                        // 17
        "Newspaper",                    // 18
        "Paper",                        // 19
        "Paper Bag",                    // 20
        "Paper Cups",                   // 21
        "Plastic Bag",                  // 22
        "Plastic Bottles",              // 23
        "Plastic Straws",               // 24
        "Ruler",                        // 25
        "Soda Cans",                    // 26
        "Styrofoam Cups",               // 27
        "Styrofoam Food Containers",    // 28
        "Tin Cans"                      // 29
    )

    val IS_RECYCLABLE = booleanArrayOf(
        true,   // 0 aerosol cans
        false,  // 1 ballpen
        false,  // 2 battery
        false,  // 3 bottle cap
        true,   // 4 cardboard
        false,  // 5 clothes
        false,  // 6 disposable plastic cutlery
        true,   // 7 eggshells
        false,  // 8 footwear
        false,  // 9 garbage bag
        true,   // 10 glass bottles
        true,   // 11 HDPE
        false,  // 12 keyboard
        false,  // 13 light bulb
        true,   // 14 magazines
        false,  // 15 mobile
        false,  // 16 monitor
        false,  // 17 mouse
        true,   // 18 newspaper
        true,   // 19 paper
        true,   // 20 paper bag
        true,   // 21 paper cups
        true,   // 22 plastic bag
        true,   // 23 plastic bottles
        false,  // 24 plastic straws
        true,   // 25 ruler
        true,   // 26 soda cans
        false,  // 27 styrofoam cups
        false,  // 28 styrofoam food containers
        true    // 29 tin cans
    )

    fun safeName(classId: Int): String =
        if (classId in NAMES.indices) NAMES[classId] else "Unknown"

    fun safeRecyclable(classId: Int): Boolean =
        if (classId in IS_RECYCLABLE.indices) IS_RECYCLABLE[classId] else false

    // Educational data (UPDATED IDs)
    private val INFO: Map<Int, WasteInfo> = mapOf(
        0 to WasteInfo(0, "Aerosol Cans", true, WasteCategory.METAL,
            "Pressurized spray can made of metal.",
            "Metal can be recycled when properly emptied.",
            "Use up contents. If empty and accepted locally, place in metal recycling. Do not puncture.",
            listOf("Empty completely", "Follow local rules"),
            listOf("Do not puncture", "Do not burn"),
            "Some areas require special handling due to pressure."
        ),

        1 to WasteInfo(1, "Ballpen", false, WasteCategory.OTHER,
            "Plastic pen with ink cartridge.",
            "Mixed plastic and ink parts are usually not accepted in recycling.",
            "Dispose as general waste unless a special pen recycling program exists.",
            listOf("Use refillable pens if possible"),
            listOf("Do not put in paper recycling"),
            "Pen plastic looks recyclable but usually isn’t accepted."
        ),

        2 to WasteInfo(2, "Battery", false, WasteCategory.EWASTE,
            "Power cell containing chemicals and metals.",
            "Can leak hazardous materials and should not go to regular bins.",
            "Bring to battery drop-off / e-waste collection.",
            listOf("Tape terminals for safety", "Store in a dry container"),
            listOf("Do not throw in general trash"),
            "Batteries require special collection."
        ),

        3 to WasteInfo(3, "Bottle Cap", false, WasteCategory.PLASTIC,
            "Small plastic/metal cap from bottles.",
            "Often too small for sorting machines; may be rejected.",
            "Follow local rules; some programs accept caps if attached to bottle.",
            listOf("Check if caps must stay on bottles"),
            listOf("Do not assume all caps are recyclable"),
            "Small size makes it hard to process."
        ),

        4 to WasteInfo(4, "Cardboard", true, WasteCategory.PAPER,
            "Thick paper packaging material.",
            "Clean, dry cardboard is widely recyclable.",
            "Flatten. Keep dry. Remove heavy food contamination.",
            listOf("Flatten boxes", "Keep dry"),
            listOf("Do not recycle oily/soaked cardboard"),
            "Pizza boxes: clean parts may be recyclable."
        ),

        5 to WasteInfo(5, "Clothes", false, WasteCategory.OTHER,
            "Textile fabric items.",
            "Most recycling bins don’t accept textiles; donate/repurpose instead.",
            "Donate if usable; otherwise textile drop-off if available.",
            listOf("Donate wearable items", "Repair/repurpose"),
            listOf("Do not put in plastic recycling"),
            "Textiles need separate collection programs."
        ),

        6 to WasteInfo(6, "Disposable Plastic Cutlery", false, WasteCategory.PLASTIC,
            "Single-use fork/spoon/knife made of plastic.",
            "Often low-value plastic and contaminated with food.",
            "Dispose as general waste; consider reusable alternatives.",
            listOf("Use reusable utensils"),
            listOf("Do not place in plastic recycling if dirty"),
            "Even if plastic, many programs reject it."
        ),

        7 to WasteInfo(7, "Eggshells", true, WasteCategory.ORGANIC,
            "Shells from eggs, organic material.",
            "Compostable and breaks down naturally.",
            "Compost (if available) or dispose as biodegradable waste.",
            listOf("Compost if possible"),
            listOf("Do not put in plastic recycling"),
            "Organic waste belongs in compost/biowaste."
        ),

        8 to WasteInfo(8, "Footwear", false, WasteCategory.OTHER,
            "Shoes/sandals with mixed materials.",
            "Mixed rubber, foam, fabric is hard to recycle.",
            "Donate if usable; otherwise dispose properly or take to special programs.",
            listOf("Donate usable pairs"),
            listOf("Do not put in regular recycling"),
            "Shoes are multi-material items."
        ),

        9 to WasteInfo(9, "Garbage Bag", false, WasteCategory.PLASTIC,
            "Thin plastic bag used to hold trash.",
            "Usually contaminated and not accepted in recycling.",
            "Dispose as general waste. Use proper bin liners as needed.",
            listOf("Tie securely to prevent litter"),
            listOf("Do not put in recycling bins"),
            "Clean plastic bags differ from trash bags."
        ),

        10 to WasteInfo(10, "Glass Bottles", true, WasteCategory.GLASS,
            "Glass container for beverages/food.",
            "Glass is highly recyclable when clean.",
            "Rinse, remove liquid. Place in glass recycling (if available).",
            listOf("Rinse bottles"),
            listOf("Do not include broken glass unless accepted"),
            "Some areas separate by color."
        ),

        // NEW: 11 HDPE
        11 to WasteInfo(11, "HDPE", true, WasteCategory.PLASTIC,
            "High-Density Polyethylene (HDPE) plastic, often marked as plastic #2.",
            "HDPE is commonly accepted and valuable in many recycling programs when clean.",
            "Empty and rinse. Recycle with plastics if accepted locally (often #2). Follow local rules.",
            listOf("Check for #2/HDPE marking", "Empty and rinse containers"),
            listOf("Do not recycle if heavily contaminated with food/chemicals"),
            "Some items are HDPE but may still be rejected due to shape/size or local rules."
        ),

        // SHIFTED IDs FROM HERE DOWN
        12 to WasteInfo(12, "Keyboard", false, WasteCategory.EWASTE,
            "Computer input device with electronics.",
            "Contains e-waste components that need special handling.",
            "Bring to e-waste collection or electronics recycler.",
            listOf("Keep e-waste separate"),
            listOf("Do not throw in general recycling"),
            "E-waste programs handle electronics safely."
        ),

        13 to WasteInfo(13, "Light Bulb", false, WasteCategory.OTHER,
            "Glass bulb with filament/LED components.",
            "Some bulbs contain hazardous materials and need special disposal.",
            "Check type: bring to special collection if required.",
            listOf("Check bulb type (LED/CFL/incandescent)"),
            listOf("Do not put in glass recycling unless allowed"),
            "CFL bulbs may contain small amounts of mercury."
        ),

        14 to WasteInfo(14, "Magazines", true, WasteCategory.PAPER,
            "Stapled paper publications.",
            "Paper fibers are recyclable.",
            "Keep dry; recycle with paper products.",
            listOf("Remove plastic wrap if present"),
            listOf("Do not recycle wet magazines"),
            "Glossy paper is usually still accepted."
        ),

        15 to WasteInfo(15, "Mobile", false, WasteCategory.EWASTE,
            "Phone with battery and circuitry.",
            "Contains valuable metals and hazardous parts.",
            "Bring to e-waste/phone trade-in programs.",
            listOf("Back up data then wipe device"),
            listOf("Do not throw in trash"),
            "Phones are e-waste, not regular recycling."
        ),

        16 to WasteInfo(16, "Monitor", false, WasteCategory.EWASTE,
            "Display screen device.",
            "May contain hazardous materials; requires e-waste disposal.",
            "Bring to e-waste collection/recycler.",
            listOf("Handle carefully to avoid breakage"),
            listOf("Do not dump illegally"),
            "Monitors should be recycled via e-waste channels."
        ),

        17 to WasteInfo(17, "Mouse", false, WasteCategory.EWASTE,
            "Computer pointing device.",
            "Electronic waste; not accepted in regular bins.",
            "Bring to e-waste recycler.",
            listOf("Collect small e-waste together"),
            listOf("Do not put in plastic recycling"),
            "Small electronics are still e-waste."
        ),

        18 to WasteInfo(18, "Newspaper", true, WasteCategory.PAPER,
            "Newsprint paper sheets.",
            "Paper fibers are recyclable.",
            "Keep clean/dry; recycle with paper.",
            listOf("Keep dry"),
            listOf("Do not recycle oily paper"),
            "Wet paper may be rejected."
        ),

        19 to WasteInfo(19, "Paper", true, WasteCategory.PAPER,
            "General paper sheets.",
            "Paper can be pulped and reused.",
            "Recycle clean/dry paper.",
            listOf("Remove heavy contamination"),
            listOf("Do not recycle soaked paper"),
            "Food-soiled paper is often not accepted."
        ),

        20 to WasteInfo(20, "Paper Bag", true, WasteCategory.PAPER,
            "Bag made of paper.",
            "Paper bags are recyclable when clean/dry.",
            "Reuse then recycle when worn out.",
            listOf("Reuse multiple times"),
            listOf("Do not recycle if soaked with oil"),
            "Wet paper bags may be rejected."
        ),

        21 to WasteInfo(21, "Paper Cups", true, WasteCategory.PAPER,
            "Disposable paper cup (often lined).",
            "Some cups are accepted depending on local facilities.",
            "If accepted locally, empty and keep as clean as possible.",
            listOf("Check local acceptance rules"),
            listOf("Do not recycle heavily soiled cups"),
            "Paper cups can have plastic lining."
        ),

        22 to WasteInfo(22, "Plastic Bag", true, WasteCategory.PLASTIC,
            "Clean plastic carry bag.",
            "Some programs accept clean bags via special drop-off.",
            "If your area accepts: keep clean/dry; use store drop-off when required.",
            listOf("Keep clean and dry"),
            listOf("Do not put in bin if local rules say drop-off only"),
            "Plastic bags can jam sorting machines."
        ),

        23 to WasteInfo(23, "Plastic Bottles", true, WasteCategory.PLASTIC,
            "Plastic bottles commonly used for drinks (often PET #1 or HDPE #2).",
            "Commonly recyclable when empty and clean.",
            "Empty, rinse, and recycle according to local rules.",
            listOf("Empty and rinse"),
            listOf("Do not leave liquid inside"),
            "Labels are usually fine; rules vary by area."
        ),

        24 to WasteInfo(24, "Plastic Straws", false, WasteCategory.PLASTIC,
            "Small plastic straw.",
            "Too small/light for sorting and often contaminated.",
            "Dispose as general waste; prefer reusable alternatives.",
            listOf("Use metal/silicone straws"),
            listOf("Do not put in recycling"),
            "Small items often get rejected."
        ),

        25 to WasteInfo(25, "Ruler", true, WasteCategory.OTHER,
            "Measuring tool (often hard plastic/wood).",
            "Some rulers are durable items; classification depends on material and local rules.",
            "If your local program accepts the material, recycle; otherwise dispose properly or reuse.",
            listOf("Reuse as long as possible"),
            listOf("Do not assume all plastic rulers are recyclable"),
            "Material type matters (wood vs plastic)."
        ),

        26 to WasteInfo(26, "Soda Cans", true, WasteCategory.METAL,
            "Aluminum beverage can.",
            "Aluminum is highly recyclable and valuable.",
            "Empty, rinse if possible, recycle with metal/aluminum.",
            listOf("Crush to save space (if allowed)"),
            listOf("Do not leave liquid inside"),
            "Aluminum recycling saves energy."
        ),

        27 to WasteInfo(27, "Styrofoam Cups", false, WasteCategory.OTHER,
            "Foam polystyrene cup.",
            "Often not accepted and hard to recycle.",
            "Dispose as general waste unless special program exists.",
            listOf("Avoid foam when possible"),
            listOf("Do not put in recycling by default"),
            "Foam is lightweight and contaminates recycling."
        ),

        28 to WasteInfo(28, "Styrofoam Food Containers", false, WasteCategory.OTHER,
            "Foam takeout container.",
            "Often not accepted and difficult to recycle.",
            "Dispose as general waste; consider reusable containers.",
            listOf("Choose reusable containers"),
            listOf("Do not place in recycling bins"),
            "Many programs reject foam containers."
        ),

        29 to WasteInfo(29, "Tin Cans", true, WasteCategory.METAL,
            "Steel/tin-plated food can.",
            "Metal cans are recyclable when cleaned.",
            "Empty, rinse, recycle with metal cans.",
            listOf("Rinse food residue"),
            listOf("Do not recycle with food still inside"),
            "Labels are usually okay; rules vary."
        )
    )

    fun info(classId: Int): WasteInfo? = INFO[classId]

    fun all(): List<WasteInfo> =
        INFO.values.sortedBy { it.classId }

    fun byCategory(category: WasteCategory): List<WasteInfo> =
        INFO.values.filter { it.category == category }.sortedBy { it.classId }

    fun search(query: String): List<WasteInfo> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return all()
        return INFO.values
            .filter { it.name.lowercase().contains(q) }
            .sortedBy { it.classId }
    }
}
