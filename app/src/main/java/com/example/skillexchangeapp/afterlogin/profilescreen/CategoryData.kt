package com.example.skillexchangeapp.afterlogin.profilescreen

object CategoryData {
    val categoryData = mapOf(
        "Arts & Creativity" to listOf("Painting", "Other"),
        "Beauty & Hair" to listOf(
            "Bodycare", "Make-up", "Eyebrows & Eyelashes", "Haircare", "Hairdressing",
            "Hairstyling", "Nails", "Personal Styling", "Skincare", "Other"
        ),
        "Business & Professional" to listOf(
            "Trading", "Admin", "Advisory & Mentoring", "Business Development", "Business Coaching",
            "Career Guidance", "Coaching", "Consultancy", "CV & Interviews", "Facilitation",
            "Learning & Development", "Public Relations", "Recruitment", "Secretarial",
            "Virtual Assistant", "Other"
        ),
        "Caretaking & Sitting" to listOf(
            "Dog-sitting", "Babysitting", "Caring for Elderly", "Dog Walking", "House Sitting",
            "Pet Sitting", "Other"
        ),
        "Cleaning & Organizing" to listOf(
            "House Cleaning", "Car Washing", "Commercial Cleaning", "Housekeeping",
            "Residential Cleaning", "Other"
        ),
        "Cooking & Dining" to listOf(
            "Baking", "Catering Services", "Chef Services", "Cooking Lessons",
            "Food Knowledge", "Other"
        ),
        "Consulting & Advisory" to listOf("Horoscope", "Other"),
        "Design & Interior" to listOf(
            "Interior Design", "Animation Design", "Branding Design", "Fashion Design",
            "Interior Decorator", "Print Design", "Other"
        ),
        "DIY & Repairs" to listOf(
            "Phone Repairing", "Alterations", "Basic Electronics", "Basic Mechanics",
            "Carpentry", "Construction", "Furniture Repair or Restoration", "Sewing",
            "Tradesperson Services", "Woodwork", "Auto Repair", "Bike Repair",
            "DIY Jobs", "Handywork", "Musical Instrument Repair", "Other"
        ),
        "Education & Coaching" to listOf(
            "Language Teaching", "Educational Support", "Homework Help", "Knowledge Exchange",
            "Mentoring", "Research Review", "Specialist Education", "Tutoring", "Other"
        ),
        "Fitness & Sports" to listOf(
            "Personal Training", "Martial Arts", "Outdoor Activities", "Pilates", "Running Partner",
            "Sport Classes", "Swimming Lessons", "Team Sports", "Water Sports", "Yoga", "Other"
        ),
        "Health & Wellness" to listOf(
            "Messaging", "Bodycare", "Coaching", "Meditation", "Mindfulness", "Nutrition",
            "Herbalism", "Holistic Therapies", "Personal Care", "Physiotherapy", "Psychology & Support",
            "Skincare", "Spiritual Consulting", "Therapies", "Treatments", "Wellness Coaching", "Other"
        ),
        "Home & Garden" to listOf(
            "Flowers and Plants", "Basic Electronics", "Carpentry and Woodwork",
            "Growing and Planting", "Handywork", "Interior and Design", "Landscaping and Construction",
            "Lawn and Hedge Care", "Painting", "Plumbing", "Other"
        ),
        "Language & Translation" to listOf(
            "Book Translation", "Arabic", "Bengali", "English", "French", "German", "Hindi",
            "Indonesian", "Irish", "Italian", "Japanese", "Mandarin", "Portuguese", "Russian",
            "Spanish", "Interpretation", "Transcription", "Translation", "Other"
        ),
        "Law & Legal Support" to listOf("Civil Cases Consulting", "Other"),
        "Music & Entertainment" to listOf(
            "Instruments Teaching", "Audio and Music Production", "Music Classes", "Music Performance",
            "Voice Cover", "Singing", "Singing Lessons", "Song Writing", "Sound Design", "Podcast", "Other"
        ),
        "Photo & Video" to listOf(
            "Photography Shooting", "Animation", "Directing", "Editing", "Film",
            "Idea Creation", "Media", "Photography", "Videography", "Other"
        ),
        "Social Media & Marketing" to listOf(
            "Social Media Managing", "Advertising", "Content Creation", "Copywriting", "Design",
            "Digital Marketing", "Editing", "Marketing", "Marketing Plans", "Social Media Support",
            "Public Relations", "Other"
        ),
        "Tech & Digital Services" to listOf(
            "Programming Teaching", "AR and VR", "Coding and Programming", "Computer Skills",
            "Crypto and NFT", "E-commerce", "Graphic Design", "IT Services", "Product Design",
            "Web Content", "Web Design", "Web Development", "Web Support", "Virtual Assistant",
            "Tech Support", "UX and UI", "Android Development", "iOS Development",
            "Mobile Development", "Other"
        ),
        "Writing & Editing" to listOf(
            "Book Revisioning", "Calligraphy", "Content Editing", "Content Writing", "Creative Writing",
            "Journal Prompting", "Lettering", "Proofreading", "Story Ending", "Other"
        ),
        "Friendship & Networking" to listOf(
            "Network Marketing", "Business Networking", "Friendship Networking",
            "Conversation and Connection", "Other"
        )
    )

    val items = categoryData.keys.toList()

}