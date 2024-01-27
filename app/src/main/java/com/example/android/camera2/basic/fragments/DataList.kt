package com.example.android.camera2.basic.fragments

val valmap :Map<String,FloatArray> = mapOf(
//        "densya" to floatArrayOf(0.5211267605633803f,0.352112676056338f,0.5586854460093896f,0.863849765258216f,0.8544600938967136f,0.8262910798122066f,0.8309859154929577f,0.8497652582159625f,0.8544600938967136f,0.6150234741784038f,0.38028169014084506f,0.3051643192488263f,0.4788732394366197f,0.7230046948356808f,0.5023474178403756f,0.5633802816901409f,0.49295774647887325f,0.755868544600939f,0.7793427230046949f,0.7417840375586855f,0.6525821596244131f,0.6103286384976526f,0.6431924882629108f,0.7323943661971831f,0.7136150234741784f,0.3380281690140845f,0.36619718309859156f,0.27699530516431925f,0.38967136150234744f,1.0f,0.8309859154929577f,0.41784037558685444f,0.4647887323943662f,0.6244131455399061f,0.4225352112676056f,0.2863849765258216f,0.19248826291079812f,0.16901408450704225f,0.15492957746478872f,0.19718309859154928f,0.6291079812206573f,0.5821596244131455f,0.4694835680751174f,0.6338028169014085f,0.4460093896713615f,0.7793427230046949f,0.9483568075117371f,0.5164319248826291f,0.2676056338028169f,0.19718309859154928f,0.023474178403755867f,0.0f,0.14084507042253522f,0.16901408450704225f,0.16901408450704225f,0.14084507042253522f,0.6103286384976526f,0.5492957746478874f,0.7746478873239436f,0.9248826291079812f,0.6713615023474179f,0.6854460093896714f,0.9530516431924883f,0.6525821596244131f,0.3004694835680751f,0.3474178403755869f,0.2300469483568075f,0.07511737089201878f,0.15492957746478872f,0.20187793427230047f,0.6431924882629108f,0.48826291079812206f,0.3145539906103286f,0.6384976525821596f,0.7417840375586855f,0.8215962441314554f,0.6995305164319249f,0.6525821596244131f,0.7793427230046949f,0.596244131455399f,0.3333333333333333f,0.647887323943662f,0.49765258215962443f,0.460093896713615f,0.596244131455399f,0.8591549295774648f,0.8262910798122066f,0.7699530516431925f,0.568075117370892f,0.7981220657276995f,0.3192488262910798f,0.5492957746478874f,0.38028169014084506f,0.215962441314554f,0.6807511737089202f,0.5586854460093896f,0.4272300469483568f,0.9812206572769953f,0.9671361502347418f,1.0f,0.9953051643192489f,0.9812206572769953f,0.8732394366197183f,0.7605633802816901f,0.6525821596244131f,0.5492957746478874f,0.07511737089201878f,0.09859154929577464f,0.06572769953051644f,0.18309859154929578f,0.676056338028169f,0.5539906103286385f,0.3286384976525822f,0.9154929577464789f,0.9859154929577465f,0.9765258215962441f,0.9812206572769953f,0.9765258215962441f,0.9859154929577465f,0.9530516431924883f,0.7417840375586855f,0.36619718309859156f,0.028169014084507043f,0.12206572769953052f,0.3145539906103286f,0.7793427230046949f,0.9436619718309859f,0.5023474178403756f,0.29107981220657275f,0.8591549295774648f,0.9859154929577465f,0.971830985915493f,0.9859154929577465f,0.9906103286384976f,0.9624413145539906f,0.8591549295774648f,0.7981220657276995f,0.4835680751173709f,0.056338028169014086f,0.1596244131455399f,0.4131455399061033f,0.7136150234741784f,0.8685446009389671f,0.47417840375586856f,0.2535211267605634f,0.704225352112676f,0.9765258215962441f,0.9342723004694836f,0.8732394366197183f,0.7981220657276995f,0.7370892018779343f,0.5774647887323944f,0.5023474178403756f,0.4272300469483568f,0.2347417840375587f,0.1784037558685446f,0.36619718309859156f,0.4835680751173709f,0.39906103286384975f,0.3380281690140845f,0.3615023474178404f,0.6431924882629108f,0.7136150234741784f,0.6901408450704225f,0.6901408450704225f,0.6291079812206573f,0.5258215962441315f,0.3755868544600939f,0.22065727699530516f,0.47417840375586856f,0.8215962441314554f,0.8497652582159625f,0.48826291079812206f,0.3380281690140845f,0.23943661971830985f,0.3286384976525822f,0.5539906103286385f,0.9812206572769953f,0.9953051643192489f,1.0f,1.0f,0.8028169014084507f,0.3615023474178404f,0.4694835680751174f,0.8403755868544601f,0.8685446009389671f,0.8779342723004695f,0.8591549295774648f,0.7417840375586855f,0.40375586854460094f,0.25821596244131456f,0.38967136150234744f,0.5117370892018779f,0.8826291079812206f,0.9154929577464789f,0.9061032863849765f,0.8685446009389671f,0.7934272300469484f,0.6244131455399061f,0.704225352112676f,0.9248826291079812f,0.9107981220657277f,0.863849765258216f,0.8873239436619719f,0.8591549295774648f,0.5821596244131455f,0.5492957746478874f,0.4835680751173709f,0.5352112676056338f,0.5774647887323944f,0.6103286384976526f,0.6103286384976526f,0.6009389671361502f,0.6901408450704225f,0.6948356807511737f,0.6150234741784038f,0.6807511737089202f,0.6291079812206573f,0.6009389671361502f,0.6948356807511737f,0.6713615023474179f,0.568075117370892f,0.6525821596244131f,0.539906103286385f,0.6525821596244131f,0.9530516431924883f,0.9859154929577465f,0.9061032863849765f,0.6619718309859155f,0.6572769953051644f,0.6572769953051644f,0.7230046948356808f,0.6431924882629108f,0.6009389671361502f,0.6948356807511737f,0.7323943661971831f,0.6244131455399061f,0.6197183098591549f,0.7417840375586855f,0.6384976525821596f,0.48826291079812206f,0.7699530516431925f,0.9295774647887324f,0.9014084507042254f,0.7793427230046949f,0.7887323943661971f,0.7605633802816901f,0.8309859154929577f,0.7981220657276995f,0.8075117370892019f,0.8450704225352113f,0.8215962441314554f,0.8215962441314554f,0.7417840375586855f,0.6666666666666666f,0.45539906103286387f,),
//        "strange" to floatArrayOf(0.20634920634920634f,0.14285714285714285f,0.15873015873015872f,0.44841269841269843f,0.1746031746031746f,0.15476190476190477f,0.3531746031746032f,0.5753968253968254f,0.4246031746031746f,0.6944444444444444f,0.9603174603174603f,0.9920634920634921f,1.0f,1.0f,1.0f,1.0f,0.1984126984126984f,0.20238095238095238f,0.4642857142857143f,0.2261904761904762f,0.39285714285714285f,0.4722222222222222f,0.19444444444444445f,0.6626984126984127f,0.6865079365079365f,0.6746031746031746f,0.9007936507936508f,0.9722222222222222f,1.0f,0.996031746031746f,1.0f,1.0f,0.19444444444444445f,0.11507936507936507f,0.01984126984126984f,0.6507936507936508f,0.6944444444444444f,0.25793650793650796f,0.4166666666666667f,0.7976190476190477f,0.8095238095238095f,0.7341269841269841f,0.8849206349206349f,0.9761904761904762f,0.9682539682539683f,0.9404761904761905f,1.0f,1.0f,0.3134920634920635f,0.42063492063492064f,0.5277777777777778f,0.503968253968254f,0.9126984126984127f,0.626984126984127f,0.43253968253968256f,0.6428571428571429f,0.6547619047619048f,0.5912698412698413f,0.873015873015873f,0.9325396825396826f,0.8968253968253969f,0.873015873015873f,1.0f,1.0f,0.26587301587301587f,0.27380952380952384f,0.75f,0.5714285714285714f,0.8214285714285714f,0.43253968253968256f,0.6746031746031746f,0.5992063492063492f,0.5753968253968254f,0.8531746031746031f,0.8253968253968254f,0.7976190476190477f,0.6349206349206349f,0.20238095238095238f,0.3888888888888889f,0.9642857142857143f,0.7619047619047619f,0.4166666666666667f,0.5158730158730159f,0.7777777777777778f,0.8492063492063492f,0.8650793650793651f,0.7579365079365079f,0.6865079365079365f,0.6547619047619048f,0.8253968253968254f,0.8214285714285714f,0.8373015873015873f,0.3333333333333333f,0.4523809523809524f,0.17857142857142858f,0.8531746031746031f,0.626984126984127f,0.1626984126984127f,0.25f,0.6031746031746031f,0.8095238095238095f,0.8690476190476191f,0.8412698412698413f,0.7777777777777778f,0.7579365079365079f,0.8174603174603174f,0.8531746031746031f,0.746031746031746f,0.30952380952380953f,0.0f,0.39285714285714285f,0.9920634920634921f,0.25793650793650796f,0.3253968253968254f,0.8531746031746031f,0.5714285714285714f,0.7380952380952381f,0.8134920634920635f,0.7777777777777778f,0.7976190476190477f,0.7341269841269841f,0.5555555555555556f,0.6349206349206349f,0.3333333333333333f,0.4246031746031746f,0.5079365079365079f,0.4126984126984127f,0.9603174603174603f,0.873015873015873f,0.376984126984127f,0.753968253968254f,0.32936507936507936f,0.5079365079365079f,0.6904761904761905f,0.626984126984127f,0.7936507936507936f,0.6825396825396826f,0.6587301587301587f,0.5476190476190477f,0.2857142857142857f,0.4166666666666667f,0.7619047619047619f,0.24206349206349206f,0.8928571428571429f,0.4126984126984127f,0.21428571428571427f,0.6984126984126984f,0.21428571428571427f,0.3134920634920635f,0.7658730158730159f,0.623015873015873f,0.3611111111111111f,0.7896825396825397f,0.8452380952380952f,0.3968253968253968f,0.7222222222222222f,0.43253968253968256f,0.2619047619047619f,0.007936507936507936f,0.3492063492063492f,0.12698412698412698f,0.10317460317460317f,0.6666666666666666f,0.10317460317460317f,0.30158730158730157f,0.6785714285714286f,0.7936507936507936f,0.7380952380952381f,0.8452380952380952f,0.7182539682539683f,0.3134920634920635f,0.6706349206349206f,0.4880952380952381f,0.23015873015873015f,0.25f,0.25f,0.7063492063492064f,0.2222222222222222f,0.38492063492063494f,0.08333333333333333f,0.42857142857142855f,0.5634920634920635f,0.5833333333333334f,0.6428571428571429f,0.5515873015873016f,0.5793650793650794f,0.4880952380952381f,0.25396825396825395f,0.49603174603174605f,0.6388888888888888f,0.38492063492063494f,0.8412698412698413f,1.0f,0.9603174603174603f,0.876984126984127f,0.2857142857142857f,0.21031746031746032f,0.6904761904761905f,0.6746031746031746f,0.5515873015873016f,0.8134920634920635f,0.8650793650793651f,0.25793650793650796f,0.36904761904761907f,0.7420634920634921f,0.2896825396825397f,0.49603174603174605f,1.0f,1.0f,1.0f,0.8492063492063492f,0.6626984126984127f,0.30952380952380953f,0.8611111111111112f,0.5753968253968254f,0.3888888888888889f,0.8373015873015873f,0.9365079365079365f,0.44047619047619047f,0.7420634920634921f,0.3412698412698413f,0.07936507936507936f,0.5833333333333334f,1.0f,0.9484126984126984f,0.9484126984126984f,0.6388888888888888f,0.43253968253968256f,0.39285714285714285f,0.9920634920634921f,0.9325396825396826f,0.7817460317460317f,0.8531746031746031f,0.9563492063492064f,0.34523809523809523f,0.49603174603174605f,0.3253968253968254f,0.6071428571428571f,0.25f,0.8611111111111112f,0.873015873015873f,0.8650793650793651f,0.7420634920634921f,0.49206349206349204f,0.47619047619047616f,0.9563492063492064f,0.9603174603174603f,0.8849206349206349f,0.7658730158730159f,0.9365079365079365f,0.40476190476190477f,0.8333333333333334f,0.7738095238095238f,0.2976190476190476f,0.27380952380952384f,0.9325396825396826f,)
        "strange" to floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f,0f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,0f,0f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,1f,1f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,1f,0f,1f,0f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,1f,1f,0f,0f,0f,0f,1f,1f,1f,1f,1f,0f,1f,0f,0f,0f,1f,0f,0f,1f,0f,1f,1f,1f,1f,1f,0f,0f,0f,0f,0f,0f,1f,1f,0f,1f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f,1f,0f,1f,0f,0f,1f,0f,0f,1f,1f,0f,1f,1f,0f,1f,0f,0f,0f,0f,0f,0f,1f,0f,0f,1f,1f,1f,1f,1f,0f,1f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f,0f,1f,0f,1f,1f,1f,1f,0f,0f,1f,1f,0f,1f,1f,0f,0f,1f,0f,0f,1f,1f,1f,1f,0f,0f,1f,1f,0f,1f,1f,0f,0f,0f,0f,0f,1f,1f,1f,1f,0f,0f,1f,1f,1f,1f,1f,0f,0f,0f,1f,0f,1f,1f,1f,1f,0f,0f,1f,1f,1f,1f,1f,0f,1f,1f,0f,0f,1f,),
        "densya" to floatArrayOf(0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,1f,1f,0f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,1f,1f,0f,0f,0f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,1f,0f,1f,1f,0f,1f,1f,1f,0f,0f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,1f,1f,1f,1f,0f,1f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,1f,1f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,0f,1f,1f,1f,1f,1f,0f,0f,0f,0f,1f,1f,0f,0f,0f,0f,0f,1f,1f,1f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,1f,0f,0f,0f,0f,1f,1f,1f,0f,0f,0f,0f,0f,0f,0f,0f,0f,1f,1f,1f,1f,1f,0f,1f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,1f,0f,0f,)


)
