package com.example.kazumasatakaoka.manpokei

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_receive.*
import org.bitcoinj.core.CashAddressFactory
import org.bitcoinj.kits.WalletAppKit
import org.bitcoinj.params.TestNet3Params
import java.io.File
import org.bitcoinj.wallet.Wallet

class ReceiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receive)


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

        balanceText.text = balance.toString()
        addressText.text = address.toString()

        val qr = BarcodeEncoder().encodeBitmap(address.toString(), BarcodeFormat.QR_CODE, 400, 400)

        qrView.setImageBitmap(qr)


    }
}
