package com.example.kazumasatakaoka.manpokei

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_receive.*
import kotlinx.android.synthetic.main.activity_send.*
import org.bitcoinj.core.CashAddressFactory
import org.bitcoinj.core.Coin
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import org.bitcoinj.wallet.Wallet
import org.jetbrains.anko.alert
import java.io.File

class SendActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        val params = TestNet3Params()
        val dir = File("context.filesDir")
        val filePrefix = "testnet"
        val kit = WalletAppKit(params, dir , filePrefix).apply {
            startAsync()
            awaitRunning()
        }
        val wallet  = kit.wallet().apply { allowSpendingUnconfirmedTransactions() }

        wallet.addCoinsReceivedEventListener { wallet, transaction, prev, new ->
        }

        val balance = wallet.getBalance(Wallet.BalanceType.ESTIMATED)
        val address = CashAddressFactory.create().getFromBase58(params, wallet.currentReceiveAddress().toBase58())

        val to = CashAddressFactory.create().getFromFormattedAddress(params, editAddress.text.toString())
        val sendAmount = Coin.parseCoin(editAmount.text.toString())

        if(balance > sendAmount){
            wallet.sendCoins(
                    kit.peerGroup(),
                    to,
                    sendAmount,
                    true
            )
            alert("Transaction send") {  }.show()}
            else{
                alert("Amount must be less than balance ") {  }.show()
            }
        }
    }
