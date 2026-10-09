# Format at a Glance

```text
MSSLB-LX/1.0
BUNDLE¦ID¦VERSION¦PROFILE¦TARGET¦ENTRY¦FRAMES¦KNOWLEDGE¦BACKEND¦NETWORK¦POLICY¦REPLAY¦ADMISSION¦ERROR¦STATE¦PROOF
(bundle ¦ ...)

MCRT-K/1.0 BEGIN F0000
ID¦PARENT¦SOURCE¦FIELD¦MODULE¦SEQ¦DEPTH¦TICK¦SYMBOL¦EXEC¦ROUTE¦LEARN¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦ADMIT¦STATUS¦PROOF
(F0000 ¦ ROOT ¦ ...)

MSSL-S/1.0
ROW¦FACE¦LANE¦OP¦OUT¦A¦B¦CAP¦WIDTH¦MODE¦AUX¦TYPE¦CLASS¦REGIME¦ASSUME¦ERROR¦CONF¦PROOF
(C0000 ¦ CODE ¦ boot ¦ MOVI ¦ R0 ¦ 5 ¦ _ ¦ C0 ¦ W ¦ wrap ¦ 0 ¦ bitvec ¦ arithmetic ¦ exact ¦ none ¦ exact ¦ 1.0 ¦ proof)
(K0001 ¦ KNOW ¦ semantics ¦ REL ¦ conditioning ¦ amplifies ¦ perturbation ¦ _ ¦ _ ¦ _ ¦ _ ¦ concept ¦ exact_numeric ¦ exact ¦ none ¦ exact ¦ 1.0 ¦ compendium)
...
END MSSL-S
END MCRT-K
@frame sha256:...

END MSSLB-LX
@bundle sha256:...
```
