package dev.davidalamo.appdpa.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage


object SupabaseManager {

    private const val SUPABASE_URL = "https://fyikfonvjconjsizrbom.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImZ5aWtmb252amNvbmpzaXpyYm9tIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTIyNzUzODIsImV4cCI6MjEwNzg1MTM4Mn0.avsw_Ixn2rca5LBSxsi5mhjSHhCZGXBDE5ODYP5bZi8"

    val client = createSupabaseClient(
        SUPABASE_URL,
        SUPABASE_ANON_KEY){
            install(Storage)
        }
}