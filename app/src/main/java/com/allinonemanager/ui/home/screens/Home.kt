package com.allinonemanager.ui.home.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.allinonemanager.ui.util.BottomWaveShape
import com.allinonemanager.ui.util.TopWaveShape
import com.allinonemanager.R

private val FigmaYellow4 = Color(0xFFFFC200)

data class ModuleItem(
    val iconResId: Int,
    val label: String,
    val route: String
)

private val modules = listOf(
    ModuleItem(R.drawable.credit_card, "Credit Card", "credit_card"),
    ModuleItem(R.drawable.bank_account, "Bank Account", "bank_account"),
    ModuleItem(R.drawable.book, "Chit Manager", "chit_manager"),
    ModuleItem(R.drawable.social_account, "Social Media", "social_media"),
    ModuleItem(R.drawable.folder_rupee, "Daily Transactions", "daily_transactions"),
    ModuleItem(R.drawable.building_construction_contrast, "Construction", "construction"),
    ModuleItem(R.drawable.ic_baseline_card_giftcard, "Reward Points", "reward_points"),
    ModuleItem(R.drawable.fluent_money_hand, "Lending", "lending"),
    ModuleItem(R.drawable.emi_manager, "EMI Manager", "emi_manager"),
    ModuleItem(R.drawable.receive_money, "Borrow Money", "borrow_money"),
    ModuleItem(R.drawable.insurance, "Insurance", "insurance")
)

@Composable
@Preview(showBackground = true)
fun Home(
    onModuleClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Top yellow header with wave bottom edge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(FigmaYellow4, shape = TopWaveShape())
                    .height(140.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(top = 24.dp, start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onAccountClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Text(
                    text = "All in one manager",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 0.dp)
                )
            }

            // Grid of module icons
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(modules) { module ->
                    ModuleGridItem(module = module, onClick = { onModuleClick(module.route) })
                }
            }
        }

        // Bottom yellow wave footer
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(100.dp)
                .background(FigmaYellow4, shape = BottomWaveShape())
        )
    }
}

@Composable
private fun ModuleGridItem(module: ModuleItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .aspectRatio(1.2f)
            .background(FigmaYellow4, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = module.iconResId),
                contentDescription = module.label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = module.label,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}