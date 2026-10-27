package com.example.library.uiscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List as ListIcon
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.library.LibraryViewModel

val LibBlue = Color(0xFF2B52A8)
val LibMaroon = Color(0xFF9B1C31)

private val tabs = listOf(
    "Quản lý" to Icons.Default.Home,
    "DS Sách" to Icons.AutoMirrored.Filled.ListIcon,
    "Sinh viên" to Icons.Default.Person
)

@Composable
fun LibraryApp(vm: LibraryViewModel = viewModel()) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showAddItem by rememberSaveable { mutableStateOf(false) }
    var showAddPerson by rememberSaveable { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(vm.message) {
        vm.message?.let {
            snackbarHost.showSnackbar(it)
            vm.consumeMessage()
        }
    }

    Scaffold(
        containerColor = Color(0xFFFAFAFA),
        snackbarHost = { SnackbarHost(snackbarHost) },
        floatingActionButton = {
            if (tab != 0) {
                FloatingActionButton(
                    onClick = { if (tab == 1) showAddItem = true else showAddPerson = true },
                    containerColor = LibBlue,
                    contentColor = Color.White
                ) { Icon(Icons.Default.Add, contentDescription = "Thêm") }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                tabs.forEachIndexed { i, (label, icon) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { tab = i },
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = LibBlue,
                            selectedTextColor = LibBlue,
                            indicatorColor = Color(0xFFE3EAFB)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                0 -> ManageScreen(vm)
                1 -> BooksScreen(vm)
                else -> PeopleScreen(vm)
            }
        }
    }

    if (showAddItem) {
        AddItemDialog(
            onDismiss = { showAddItem = false },
            onConfirm = { type, title, author, copies ->
                vm.addItem(type, title, author, copies)
                showAddItem = false
            }
        )
    }
    if (showAddPerson) {
        AddPersonDialog(
            onDismiss = { showAddPerson = false },
            onConfirm = { name ->
                vm.addPerson(name)
                showAddPerson = false
            }
        )
    }
}