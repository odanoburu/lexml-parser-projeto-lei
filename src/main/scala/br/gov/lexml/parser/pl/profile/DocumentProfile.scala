package br.gov.lexml.parser.pl.profile
import scala.util.matching.Regex
import scala.language.postfixOps

trait RegexProfile {
  def regexLocalData : List[Regex] = List()
	def regexJustificativa : List[Regex] = List()
	def regexAnexos : List[Regex] = List()
	def regexLegislacaoCitada : List[Regex] = List()
	def regexAssinatura : List[Regex] = List()
	def regexEpigrafe : List[Regex] = List()  
	def regexPosEpigrafe : List[Regex] = List()
	def epigrafeObrigatoria : Boolean = true
	def preEpigrafePermitida : Boolean = true
	def regexPreambulo : List[Regex] = List()
}

trait EpigrafeOpcional extends RegexProfile {
  override def epigrafeObrigatoria = false
}

trait PreEpigrafeProibida extends RegexProfile {
  override def preEpigrafePermitida = false
}


trait DefaultRegexProfile extends RegexProfile {
	override def regexLocalData = super.regexLocalData ++ List(
	      "^sala da sessao"r,
	      "^sala das sessoes"r,
	      "^sala das comissoes"r,
	      "^sala da comissao"r,
	      "^camara dos deputados"r,
	      "^senado federal"r,
	      "^brasilia,"r
	)
	override def regexJustificativa = super.regexJustificativa ++ List(
	    "^justificacao"r,
	    "^j u s t i f i c a c a o"r,
	    "^justificativa"r,
	    "^j u s t i f i c a t i v a"r
	)
	override def regexAnexos = super.regexAnexos ++ List(
	    "^anexo"r,
	    "a n e x o"r
	)
	override def regexLegislacaoCitada = super.regexLegislacaoCitada ++ List(
	    "^legislacao citada"r,
	    "^l e g i s l a c a o c i t a d a"r
	)
	override def regexAssinatura = super.regexAssinatura ++ List("^senadora? "r)
	override def regexEpigrafe = super.regexEpigrafe ++ List(
          """^\s*(red\d+;+)?(projeto( de)? (lei|decreto legislativo)|(proposta|projeto) de emenda|pec|projeto de resolu)""".r
         ,"""^(n[oº°˚]|complementar)"""r
    )
    
    override def regexPosEpigrafe = super.regexPosEpigrafe ++ List(
        """^\s*(\(.*\)|autora?:.*|autoria d.*|(d[oa] )?senador.*)\s*$""".r
    )
    
    override def regexPreambulo = super.regexPreambulo ++ List(
        "^o (congress+o nacional|senado federal) (decret[oa]|resolve|promulg[oa])"r,
        "^[ao] president[ae] da republica"r,
        "^[ao] vice-president[ae] da republica"r,
        "^as? mesas?"r
       )
}

trait TipoNormaProfile {
  def urnFragTipoNorma : String
  def epigrafeHead : String  
  def epigrafeTail : String = ""
}

trait AutoridadeProfile {
  def urnFragAutoridade : String
  def autoridadeEpigrafe : Option[String] = None
}

trait LocalidadeProfile {
  def urnFragLocalidade : Option[String] = None
}

trait LocalidadeBR extends LocalidadeProfile {
  override def urnFragLocalidade = Some("br")
}

trait DocumentProfile extends RegexProfile with TipoNormaProfile with AutoridadeProfile with LocalidadeProfile {
  lazy val subTipoNorma = urnFragTipoNorma.split(";") match {
    case Array(_,st) => Some(st)
    case _ => None
  }
  def +(o : Overrides) =
      DocumentProfileOverride(this).replaceOverrides(o)
}

trait Overrides {
  val overrideRegexLocalData: Option[List[Regex]]
  val overrideRegexJustificativa: Option[List[Regex]]
  val overrideRegexAnexos: Option[List[Regex]]
  val overrideRegexLegislacaoCitada: Option[List[Regex]]
  val overrideRegexAssinatura: Option[List[Regex]]
  val overrideRegexEpigrafe: Option[List[Regex]]
  val overrideRegexPosEpigrafe: Option[List[Regex]]
  val overrideEpigrafeObrigatoria: Option[Boolean]
  val overridePreEpigrafePermitida: Option[Boolean]
  val overrideRegexPreambulo: Option[List[Regex]]
  val overrideUrnFragTipoNorma: Option[String]
  val overrideEpigrafeHead: Option[String]
  val overrideEpigrafeTail: Option[String]
  val overrideUrnFragAutoridade: Option[String]
  val overrideAutoridadeEpigrafe: Option[Option[String]]
  val overrideUrnFragLocalidade : Option[Option[String]]
}

final case class OverridesData(
  overrideRegexLocalData: Option[List[Regex]] = None,
  overrideRegexJustificativa: Option[List[Regex]] = None,
  overrideRegexAnexos: Option[List[Regex]] = None,
  overrideRegexLegislacaoCitada: Option[List[Regex]] = None,
  overrideRegexAssinatura: Option[List[Regex]] = None,
  overrideRegexEpigrafe: Option[List[Regex]] = None,
  overrideRegexPosEpigrafe: Option[List[Regex]] = None,
  overrideEpigrafeObrigatoria: Option[Boolean] = None,
  overridePreEpigrafePermitida: Option[Boolean] = None,
  overrideRegexPreambulo: Option[List[Regex]] = None,
  overrideUrnFragTipoNorma: Option[String] = None,
  overrideEpigrafeHead: Option[String] = None,
  overrideEpigrafeTail: Option[String] = None,
  overrideUrnFragAutoridade: Option[String] = None,
  overrideAutoridadeEpigrafe: Option[Option[String]] = None,
  overrideUrnFragLocalidade : Option[Option[String]] = None
) extends Overrides

final case class DocumentProfileOverride(base : DocumentProfile,
  overrideRegexLocalData: Option[List[Regex]] = None,
  overrideRegexJustificativa: Option[List[Regex]] = None,
  overrideRegexAnexos: Option[List[Regex]] = None,
  overrideRegexLegislacaoCitada: Option[List[Regex]] = None,
  overrideRegexAssinatura: Option[List[Regex]] = None,
  overrideRegexEpigrafe: Option[List[Regex]] = None,
  overrideRegexPosEpigrafe: Option[List[Regex]] = None,
  overrideEpigrafeObrigatoria: Option[Boolean] = None,
  overridePreEpigrafePermitida: Option[Boolean] = None,
  overrideRegexPreambulo: Option[List[Regex]] = None,
  overrideUrnFragTipoNorma: Option[String] = None,
  overrideEpigrafeHead: Option[String] = None,
  overrideEpigrafeTail: Option[String] = None,
  overrideUrnFragAutoridade: Option[String] = None,
  overrideAutoridadeEpigrafe: Option[Option[String]] = None,
  overrideUrnFragLocalidade : Option[Option[String]] = None
) extends DocumentProfile with Overrides {
  override final def regexLocalData : List[Regex] = overrideRegexLocalData.getOrElse(base.regexLocalData)
  override final def regexJustificativa: List[Regex] = overrideRegexJustificativa.getOrElse(base.regexJustificativa)
  override final def regexAnexos: List[Regex] = overrideRegexAnexos.getOrElse(base.regexAnexos)
  override final def regexLegislacaoCitada: List[Regex] = overrideRegexLegislacaoCitada.getOrElse(base.regexLegislacaoCitada)
  override final def regexAssinatura: List[Regex] = overrideRegexAssinatura.getOrElse(base.regexAssinatura)
  override final def regexEpigrafe: List[Regex] = overrideRegexEpigrafe.getOrElse(base.regexEpigrafe)
  override final def regexPosEpigrafe: List[Regex] = overrideRegexPosEpigrafe.getOrElse(base.regexPosEpigrafe)
  override final def epigrafeObrigatoria: Boolean = overrideEpigrafeObrigatoria.getOrElse(base.epigrafeObrigatoria)
  override final def preEpigrafePermitida: Boolean = overridePreEpigrafePermitida.getOrElse(base.preEpigrafePermitida)
  override final def regexPreambulo: List[Regex] = overrideRegexPreambulo.getOrElse(base.regexPreambulo)
  override final def urnFragTipoNorma: String = overrideUrnFragTipoNorma.getOrElse(base.urnFragTipoNorma)
  override final def epigrafeHead: String = overrideEpigrafeHead.getOrElse(base.epigrafeHead)
  override final def epigrafeTail: String = overrideEpigrafeTail.getOrElse(base.epigrafeTail)
  override final def urnFragAutoridade: String = overrideUrnFragAutoridade.getOrElse(base.urnFragAutoridade)
  override final def autoridadeEpigrafe: Option[String] = overrideAutoridadeEpigrafe.getOrElse(base.autoridadeEpigrafe)
  override final def urnFragLocalidade : Option[String] = overrideUrnFragLocalidade.getOrElse(base.urnFragLocalidade)
  final val hasOverride : Boolean = this.productIterator.exists { _.isInstanceOf[Some[_]] }
  
  override def +(o : Overrides) = copy(
    overrideRegexLocalData = o.overrideRegexLocalData.orElse(overrideRegexLocalData),
    overrideRegexJustificativa = o.overrideRegexJustificativa.orElse(overrideRegexJustificativa),
    overrideRegexAnexos = o.overrideRegexAnexos.orElse(overrideRegexAnexos),
    overrideRegexLegislacaoCitada = o.overrideRegexLegislacaoCitada.orElse(overrideRegexLegislacaoCitada),
    overrideRegexAssinatura = o.overrideRegexAssinatura.orElse(overrideRegexAssinatura),
    overrideRegexEpigrafe = o.overrideRegexEpigrafe.orElse(overrideRegexEpigrafe),
    overrideRegexPosEpigrafe = o.overrideRegexPosEpigrafe.orElse(overrideRegexPosEpigrafe),
    overrideEpigrafeObrigatoria = o.overrideEpigrafeObrigatoria.orElse(overrideEpigrafeObrigatoria),
    overridePreEpigrafePermitida = o.overridePreEpigrafePermitida.orElse(overridePreEpigrafePermitida),
    overrideRegexPreambulo = o.overrideRegexPreambulo.orElse(overrideRegexPreambulo),
    overrideUrnFragTipoNorma = o.overrideUrnFragTipoNorma.orElse(overrideUrnFragTipoNorma),
    overrideEpigrafeHead = o.overrideEpigrafeHead.orElse(overrideEpigrafeHead),
    overrideEpigrafeTail = o.overrideEpigrafeTail.orElse(overrideEpigrafeTail),
    overrideUrnFragAutoridade = o.overrideUrnFragAutoridade.orElse(overrideUrnFragAutoridade),
    overrideAutoridadeEpigrafe = o.overrideAutoridadeEpigrafe.orElse(overrideAutoridadeEpigrafe),
    overrideUrnFragLocalidade  = o.overrideUrnFragLocalidade .orElse(overrideUrnFragLocalidade)
  )
  
  def replaceOverrides(o : Overrides) = copy(
    overrideRegexLocalData = o.overrideRegexLocalData,
    overrideRegexJustificativa = o.overrideRegexJustificativa,
    overrideRegexAnexos = o.overrideRegexAnexos,
    overrideRegexLegislacaoCitada = o.overrideRegexLegislacaoCitada,
    overrideRegexAssinatura = o.overrideRegexAssinatura,
    overrideRegexEpigrafe = o.overrideRegexEpigrafe,
    overrideRegexPosEpigrafe = o.overrideRegexPosEpigrafe,
    overrideEpigrafeObrigatoria = o.overrideEpigrafeObrigatoria,
    overridePreEpigrafePermitida = o.overridePreEpigrafePermitida,
    overrideRegexPreambulo = o.overrideRegexPreambulo,
    overrideUrnFragTipoNorma = o.overrideUrnFragTipoNorma,
    overrideEpigrafeHead = o.overrideEpigrafeHead,
    overrideEpigrafeTail = o.overrideEpigrafeTail,
    overrideUrnFragAutoridade = o.overrideUrnFragAutoridade,
    overrideAutoridadeEpigrafe = o.overrideAutoridadeEpigrafe,
    overrideUrnFragLocalidade  = o.overrideUrnFragLocalidade
  )
}



object DocumentProfileRegister {  
  type Autoridade = String
  type TipoNorma = String
  type Localidade = Option[String]
  var profiles : Map[(Localidade,Autoridade,TipoNorma),DocumentProfile] = Map()
  def register(profile : DocumentProfile) = { profiles = profiles + ((profile.urnFragLocalidade,profile.urnFragAutoridade,profile.urnFragTipoNorma) -> profile) }
  def getProfile(autoridade : Autoridade, tipoNorma : TipoNorma, localidade : Option[String] = None) : Option[DocumentProfile] = profiles.get((localidade,autoridade,tipoNorma))
  def profileByAutoridadeSigla(autoridade : Autoridade, sigla : String) =
    profiles.filterKeys({ case (_,aut,tn) => aut == autoridade && tn.endsWith(";" + sigla)}).values.headOption
  def autoridades = profiles.keySet.map(_._2)
  def tiposDeNormasPorAutoridade(autoridade : String) : Set[String] = for { (_,aut,tn) <- profiles.keySet ; if aut == autoridade } yield tn
  def tiposDeNormas : Set[String] = profiles.keySet.map(_._2)
  def byUrnFrag(urnFrag : String) : Option[DocumentProfile] = urnFrag.split(":").toList match {
    case autoridade :: tipoNorma :: _ => getProfile(autoridade,tipoNorma)
    case _ => None
  }   
  val builtins = List[DocumentProfile](
      ProjetoDeLeiDoSenadoNoSenado,
      ProjetoDeLeiDaCamaraNoSenado,
      PropostaEmendaConstitucionalNoSenado,
      ProjetoDeResolucaoDoSenado,
      ProjetoDeDecretoLegislativoDoSenadoNoSenado,
      ProjetoDeDecretoLegislativoDaCamaraNoSenado,
      ProjetoDeLeiComplementarDoSenadoNoSenado,
      ProjetoDeLeiComplementarDaCamaraNoSenado,
      ProjetoDeLeiNaCamara,
      ProjetoDeLeiComplementarNaCamara,
      ProjetoDeResolucaoNaCamara,
      MedidaProvisoriaNoCongresso,
      MedidaProvisoriaFederal,
      Lei,
      LeiComplementar,
      LeiDelegada,
      DecretoLei,
      Decreto,
      EmendaConstitucional,
      ProjetoDeLeiDoSenadoNoSenado
  )
  builtins foreach register
}

trait DoSenadoProfile extends AutoridadeProfile with LocalidadeBR {
  override def urnFragAutoridade = "senado.federal"
  override def autoridadeEpigrafe = Some("DO SENADO FEDERAL")     
}

trait DaCamaraProfile extends AutoridadeProfile with LocalidadeBR {
  override def urnFragAutoridade = "camara.deputados"
  override def autoridadeEpigrafe = Some("DA CÂMARA DOS DEPUTADOS")  
}

trait DoCongressoProfile extends AutoridadeProfile with LocalidadeBR {
  override def urnFragAutoridade = "congresso.nacional"
  override def autoridadeEpigrafe = Some("DO CONGRESSO NACIONAL")
}

trait FederalProfile extends AutoridadeProfile with LocalidadeBR {
  override def urnFragAutoridade = "federal"
  override def autoridadeEpigrafe = Some("FEDERAL")  
}

object Lei extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "lei"
  override def epigrafeHead = "LEI"
}

object LeiComplementar extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "lei.complementar"
  override def epigrafeHead = "LEI COMPLEMENTAR"
}

object LeiDelegada extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "lei.delegada"
  override def epigrafeHead = "LEI DELEGADA"
}

object DecretoLei extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "decreto.lei"
  override def epigrafeHead = "DECRETO-LEI"
}

object Decreto extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "decreto"
  override def epigrafeHead = "DECRETO"
}

object EmendaConstitucional extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "emenda.constitucional"
  override def epigrafeHead = "EMENDA CONSTITUICIONAL"
}

object ProjetoDeLeiDoSenadoNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile  {
  override def urnFragTipoNorma = "projeto.lei;pls"
  override def epigrafeHead = "PROJETO DE LEI DO SENADO"
} 

object ProjetoDeLeiDaCamaraNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.lei;plc"
  override def epigrafeHead = "PROJETO DE LEI DA CÂMARA"
  override def regexEpigrafe = List()
  //override def regexPosEpigrafe = List()
}

object PropostaEmendaConstitucionalNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile {
  override def urnFragTipoNorma = "proposta.emenda.constitucional;pec"
  override def epigrafeHead = "PROPOSTA DE EMENDA CONSTITUCIONAL"
}

object ProjetoDeResolucaoDoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile {
  override def urnFragTipoNorma = "projeto.resolucao;prs"
  override def epigrafeHead = "PROJETO DE RESOLUÇÃO DO SENADO"
}

object ProjetoDeDecretoLegislativoDoSenadoNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile with PreEpigrafeProibida with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.decreto.legislativo;pds"
  override def epigrafeHead = "PROJETO DE DECRETO LEGISLATIVO (SF)"
} 

object ProjetoDeDecretoLegislativoDaCamaraNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.decreto.legislativo;pdc"
  override def epigrafeHead = "PROJETO DE DECRETO LEGISLATIVO"
}

object ProjetoDeLeiComplementarDoSenadoNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile {
  override def urnFragTipoNorma = "projeto.lei.complementar;pls"
  override def epigrafeHead = "PROJETO DE LEI DO SENADO"
  override def epigrafeTail = " - COMPLEMENTAR"
}

object ProjetoDeLeiComplementarDaCamaraNoSenado extends DocumentProfile with DefaultRegexProfile with DoSenadoProfile with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.lei.complementar;plc"
  override def epigrafeHead = "PROJETO DE LEI DA CÂMARA"
  override def epigrafeTail = " - COMPLEMENTAR"
}

object ProjetoDeLeiNaCamara extends DocumentProfile with DefaultRegexProfile with DaCamaraProfile with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.lei;pl"
  override def epigrafeHead = "PROJETO DE LEI"
}

object ProjetoDeLeiComplementarNaCamara extends DocumentProfile with DefaultRegexProfile with DaCamaraProfile with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.lei.complementar;plp"
  override def epigrafeHead = "PROJETO DE LEI COMPLEMENTAR"
}

object ProjetoDeResolucaoNaCamara extends DocumentProfile with DefaultRegexProfile with DaCamaraProfile with EpigrafeOpcional {
  override def urnFragTipoNorma = "projeto.resolucao"
  override def epigrafeHead = "PROJETO DE RESOLUÇÃO"
}

object MedidaProvisoriaNoCongresso extends DocumentProfile with DefaultRegexProfile with DoCongressoProfile {
  override def urnFragTipoNorma = "medida.provisoria;mpv"
  override def epigrafeHead = "MEDIDA PROVISÓRIA"
  override def regexEpigrafe = super.regexEpigrafe ++ List("^medida provisoria"r)  
}

object MedidaProvisoriaFederal extends DocumentProfile with DefaultRegexProfile with FederalProfile {
  override def urnFragTipoNorma = "medida.provisoria"
  override def epigrafeHead = "MEDIDA PROVISÓRIA"
  override def regexEpigrafe = super.regexEpigrafe ++ List("^medida provisoria"r)  
}

